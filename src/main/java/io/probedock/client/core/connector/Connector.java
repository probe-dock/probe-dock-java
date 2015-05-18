package io.probedock.client.core.connector;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.config.ServerConfiguration;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.common.utils.Constants;
import io.probedock.client.commons.optimize.OptimizerStore;
import io.probedock.client.core.cache.CacheOptimizerStore;
import io.probedock.client.core.serializer.ProbeSerializer;
import io.probedock.client.core.serializer.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

/**
 * Connector to send the payloads to Probe Dock.
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class Connector {
	private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);

	private static final String CONTENT_TYPE = "application/vnd.probe-dock.payload.v1+json";

	private static final int CONNECTION_TIMEOUT = 10000;

	private Configuration configuration;

	private ProbeSerializer serializer;

	/**
	 * Optimization store
	 */
	private OptimizerStore store = null;

	/**
	 * Constructor
	 *
	 * @param configuration Configuration
	 */
	public Connector(Configuration configuration) {
		this.configuration = configuration;
		this.serializer = new JsonSerializer();
	}

	/**
	 * Send a payload to Probe Dock
	 *
	 * @param testRun The test run to send
	 * @return True if the test run successfully sent to Probe Dock
	 * @throws MalformedURLException
	 */
	public boolean send(ProbeTestRun testRun) throws MalformedURLException {
		LOGGER.info("Connected to Probe Dock API at {}", configuration.getServerConfiguration().getApiUrl());

		// Print the payload to the outout stream
		if (configuration.isPayloadPrint()) {
			try (OutputStreamWriter testRunOsw = new OutputStreamWriter(System.out)) {
				serializer.serializePayload(testRunOsw, testRun, true);
			}
			catch (IOException ioe) {}
		}

		// Try to send the test run optimized
		optimizeStart();
		boolean result = sendTestRun(optimize(testRun), true);
		optimizeStop(result);

		// If the payload was not sent optimized, try to send it non-optimized
		if (!result) {
			result = sendTestRun(testRun, false);
		}

		return result;
	}

	private URL getTestRunUrl() throws MalformedURLException {
		return new URL(configuration.getServerConfiguration().getApiUrl() + "/publish");
	}

	/**
	 * Internal method to send the test run to Probe Dock agnostic to the payload optimization
	 *
	 * @param testRun The test run to send to Probe Dock
	 * @param optimized Define if the test run is optimized or not
	 * @return True if the test run was sent successfully
	 */
	private boolean sendTestRun(ProbeTestRun testRun, boolean optimized) {

		HttpURLConnection conn = null;
		final String payloadLogString = optimized ? "optimized payload" : "payload";

		try {
			conn = uploadTestRun(testRun);
 
			if (conn.getResponseCode() == 202) {
				LOGGER.info("The {} was successfully sent to Probe Dock.", payloadLogString);
				return true;
			} else {
				LOGGER.error("Unable to send the {} to Probe Dock. Return code: {}, content: {}", payloadLogString, conn.getResponseCode(), readInputStream(conn.getInputStream()));
			}
		} catch (IOException ioe) {
			if (!configuration.isPayloadPrint()) {
				try (OutputStreamWriter baos = new OutputStreamWriter(new ByteArrayOutputStream(), Charset.forName(Constants.ENCODING).newEncoder())) {
					serializer.serializePayload(baos, testRun, true);

					LOGGER.error("The {} in error: {}", payloadLogString, baos.toString());
				}
				catch (IOException baosIoe) {}

				if (conn != null) {
					try {
						if (conn.getErrorStream() != null) {
							LOGGER.error("Unable to send the {} to Probe Dock. Error: {}", payloadLogString, readInputStream(conn.getErrorStream()));
						} else {
							LOGGER.error("Unable to send the " + payloadLogString + " to Probe Dock. This is probably due to an unreachable network issue.", ioe);
						}
					} catch (IOException errorIoe) {
						LOGGER.error("Unable to send the {} to Probe Dock for unknown reason.", payloadLogString);
					}
				} else {
					LOGGER.error("Unable to send the {} to Probe Dock. Error: {}", payloadLogString, ioe.getMessage());
				}
			}
		}

		return false;
	}

	private HttpURLConnection uploadTestRun(final ProbeTestRun testRun) throws IOException {
		final HttpURLConnection conn = openConnection(configuration.getServerConfiguration(), getTestRunUrl());

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + "; charset=" + Constants.ENCODING);
		conn.setRequestProperty("Authorization", "Bearer " + configuration.getServerConfiguration().getApiToken());

		conn.setConnectTimeout(CONNECTION_TIMEOUT);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Create an output stream writer in specific encoding
		final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), Charset.forName(Constants.ENCODING).newEncoder());
		serializer.serializePayload(osw, testRun, false);

		return conn;
	}

	private String readInputStream(final InputStream in) throws IOException {

		final BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName(Constants.ENCODING).newDecoder()));
		final StringBuilder builder = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) {
			builder.append(line);
		}

		return builder.toString();
	}

	/**
	 * Start the optimization
	 */
	private void optimizeStart() {
		if (configuration.isPayloadCache()) {

			if (configuration.getOptimizerStoreClass() == null) {
				store = new CacheOptimizerStore();
				store.start(configuration);
				return;
			}

			try {
				store = (OptimizerStore) Class.forName(configuration.getOptimizerStoreClass()).newInstance();
				store.start(configuration);
			} catch (ClassNotFoundException cnfe) {
				LOGGER.warn("Unable to find the class {}. The payload will be sent without optimizations.", configuration.getOptimizerStoreClass());
			} catch (InstantiationException ex) {
				LOGGER.warn("Unable to instantiate the class {}. Concrete class required.", configuration.getOptimizerStoreClass());
			} catch (IllegalAccessException ex) {
				LOGGER.warn("Unable to instantiate the class {}. Empty constructor required.", configuration.getOptimizerStoreClass());
			}
		}
	}

	/**
	 * Process the payload optimization
	 *
	 * @param payload The payload not optimized
	 * @return The payload optimized
	 */
	private ProbeTestRun optimize(ProbeTestRun payload) {
		if (store != null) {
			return payload.getOptimizer().optimize(store, payload);
		}

		return payload;
	}

	/**
	 * Stop the optimization
	 *
	 * @param persist Define if the cache must be persisted or not
	 */
	private void optimizeStop(boolean persist) {
		if (store != null) {
			store.stop(persist);
		}
	}
	
	/**
	 * Open a connection regarding the configuration and the URL
	 * 
	 * @param configuration The configuration to get the proxy information if necessary
	 * @param url The URL to open the connection from
	 * @return The opened connection
	 * @throws IOException In case of error when opening the connection
	 */
	private HttpURLConnection openConnection(ServerConfiguration configuration, URL url) throws IOException {
		if (configuration.hasProxyConfiguration()) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(configuration.getProxyConfiguration().getHost(), configuration.getProxyConfiguration().getPort()));
			return (HttpURLConnection) url.openConnection(proxy);
		}
		else {
			return (HttpURLConnection) url.openConnection();
		}
	}
}

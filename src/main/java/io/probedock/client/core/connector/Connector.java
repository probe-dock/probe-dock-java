package io.probedock.client.core.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private static final String API_ROOT_MEDIA_TYPE = "application/hal+json";
	private static final String API_TEST_PAYLOAD_MEDIA_TYPE = "application/vnd.lotaris.rox.payload.v1+json";
	private static final String API_ROOT_TEST_PAYLOAD_LINK = "v1:test-payloads";
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
	 * Send a payload to ROX
	 *
	 * @param payload The payload to send
	 * @return True if the payload successfully sent to ROX
	 * @throws MalformedURLException
	 */
	public boolean send(ProbeTestRun payload) throws MalformedURLException {

		final URL payloadResourceUrl = getPayloadResourceUrl();
		if (payloadResourceUrl == null) {
			return false;
		}

		LOGGER.info("Connected to Probe Dock API at {}", configuration.getServerConfiguration().getApiUrl());

		// Print the payload to the outout stream
		if (configuration.isPayloadPrint()) {
			OutputStreamWriter payloadOsw = null;
			try {
				payloadOsw = new OutputStreamWriter(System.out);
				serializer.serializePayload(payloadOsw, payload, true);
			} catch (IOException ioe) {
			} finally {
				if (payloadOsw != null) {
					try {
						payloadOsw.close();
					} catch (IOException closeIoe) {
					}
				}
			}
		}

		// Try to send the payload optimized
		optimizeStart();
		boolean result = sendPayload(payloadResourceUrl, optimize(payload), true);
		optimizeStop(result);

		// If the payload was not sent optimized, try to send it non-optimized
		if (!result) {
			result = sendPayload(payloadResourceUrl, payload, false);
		}

		return result;
	}

	private URL getPayloadResourceUrl() {

		final URL url;
		try {
			url = new URL(configuration.getServerConfiguration().getApiUrl());
		} catch (MalformedURLException ex) {
			LOGGER.error("The selected Probe Dock server's API URL is not a valid URL.", ex);
			return null;
		}

		try {
			final HttpURLConnection conn = openConnection(configuration.getServerConfiguration(), url);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", API_ROOT_MEDIA_TYPE + "; charset=" + Constants.ENCODING);
			configuration.getServerConfiguration().configureAuthentication(conn);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setDoInput(true);

			if (conn.getResponseCode() == 401) {
				LOGGER.error("Authentication to Probe Dock failed with API key {}."
						+ " Make sure the API key identifier and shared secret in your ROX configuration file are correct.",
						configuration.getServerConfiguration().getApiToken());
				return null;
			}

			try {
				return new URL(parsePayloadResourceLinkHref(new InputStreamReader(conn.getInputStream(), Charset.forName(Constants.ENCODING).newDecoder())));
			} catch (MalformedURLException mue) {
				LOGGER.error("The " + API_ROOT_TEST_PAYLOAD_LINK + " link returned by the Probe Dock API is not a valid URL", mue);
				return null;
			}
		} catch (IOException ioe) {
			LOGGER.error("Could not read the Probe Dock API response", ioe);
			return null;
		}
	}

	private String parsePayloadResourceLinkHref(Reader in) throws IOException {

		final JsonNode apiRoot = new ObjectMapper().readTree(in);

		final JsonNode link = apiRoot.path("_links").path(API_ROOT_TEST_PAYLOAD_LINK).path("href");
		if (link.isMissingNode()) {
			throw new IllegalArgumentException("Expected HAL+JSON API root to have a " + API_ROOT_TEST_PAYLOAD_LINK + " link");
		}

		return link.textValue();
	}

	/**
	 * Internal method to send the payload to Probe Dock agnostic to the payload optimization
	 *
	 * @param payload The payload to send to Probe Dock
	 * @param optimized Define if the payload is optimized or not
	 * @return True if the payload was sent successfully
	 */
	private boolean sendPayload(URL payloadResourceUrl, ProbeTestRun payload, boolean optimized) {

		HttpURLConnection conn = null;
		final String payloadLogString = optimized ? "optimized payload" : "payload";

		try {
			conn = uploadPayload(payloadResourceUrl, payload);
 
			if (conn.getResponseCode() == 202) {
				LOGGER.info("The {} was successfully sent to Probe Dock.", payloadLogString);
				return true;
			} else {
				LOGGER.error("Unable to send the {} to Rox. Return code: {}, content: {}", payloadLogString, conn.getResponseCode(), readInputStream(conn.getInputStream()));
			}
		} catch (IOException ioe) {
			if (!configuration.isPayloadPrint()) {
				OutputStreamWriter baos = null;
				try {
					baos = new OutputStreamWriter(new ByteArrayOutputStream(), Charset.forName(Constants.ENCODING).newEncoder());

					serializer.serializePayload(baos, payload, true);

					LOGGER.error("The {} in error: {}", payloadLogString, baos.toString());
				} catch (IOException baosIoe) {
				} finally {
					try {
						if (baos != null) {
							baos.close();
						}
					} catch (IOException baosIoe) {
					}
				}

				if (conn != null) {
					try {
						if (conn.getErrorStream() != null) {
							LOGGER.error("Unable to send the {} to ROX. Error: {}", payloadLogString, readInputStream(conn.getErrorStream()));
						} else {
							LOGGER.error("Unable to send the " + payloadLogString + " to ROX. This is probably due to an unreachable network issue.", ioe);
						}
					} catch (IOException errorIoe) {
						LOGGER.error("Unable to send the {} to ROX for unknown reason.", payloadLogString);
					}
				} else {
					LOGGER.error("Unable to send the |{} to ROX. Error: {}", payloadLogString, ioe.getMessage());
				}
			}
		}

		return false;
	}

	private HttpURLConnection uploadPayload(final URL payloadResourceUrl, final ProbeTestRun payload) throws IOException {

		final HttpURLConnection conn = openConnection(configuration.getServerConfiguration(), payloadResourceUrl);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", API_TEST_PAYLOAD_MEDIA_TYPE + "; charset=" + Constants.ENCODING);
		configuration.getServerConfiguration().configureAuthentication(conn);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Create an output stream writer in specific encoding
		final OutputStreamWriter osw =
				new OutputStreamWriter(conn.getOutputStream(), Charset.forName(Constants.ENCODING).newEncoder());
		serializer.serializePayload(osw, payload, false);

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

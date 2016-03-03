package io.probedock.client.core.connector;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.config.ServerConfiguration;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.common.utils.Constants;
import io.probedock.client.core.serializer.ProbeSerializer;
import io.probedock.client.core.serializer.json.JsonSerializer;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connector to send the payloads to Probe Dock.
 * 
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class Connector {
	private static final Logger LOGGER = Logger.getLogger(Connector.class.getCanonicalName());

	private static final String CONTENT_TYPE = "application/vnd.probe-dock.payload.v1+json";

	private static final int CONNECTION_TIMEOUT = 10000;

	private Configuration configuration;

	private ProbeSerializer serializer;

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
		LOGGER.info("Connected to Probe Dock API at " + configuration.getServerConfiguration().getApiUrl());

		// Print the payload to the outout stream
		if (configuration.isPayloadPrint()) {
			try (OutputStreamWriter testRunOsw = new OutputStreamWriter(System.out)) {
				serializer.serializePayload(testRunOsw, testRun, true);
			}
			catch (IOException ioe) {}
		}

		return sendTestRun(testRun);
	}

	/**
	 * @return The Probe Dock URL where to publish the test run
	 * @throws MalformedURLException If the server base URL is malformed.
	 */
	private URL getTestRunUrl() throws MalformedURLException {
		return new URL(configuration.getServerConfiguration().getApiUrl() + "/publish");
	}

	/**
	 * Internal method to send the test run to Probe Dock agnostic to the payload optimization
	 *
	 * @param testRun The test run to send to Probe Dock
	 * @return True if the test run was sent successfully
	 */
	private boolean sendTestRun(ProbeTestRun testRun) {

		HttpURLConnection conn = null;

		try {
			conn = uploadTestRun(testRun);
 
			if (conn.getResponseCode() == 202) {
				LOGGER.info("The test run was successfully sent to Probe Dock.");
				return true;
			} else {
				LOGGER.severe("Unable to send the test run to Probe Dock. Return code: " + conn.getResponseCode() + ", content: " + readInputStream(conn.getInputStream()));
			}
		} catch (IOException ioe) {
			if (!configuration.isPayloadPrint()) {
				try (OutputStreamWriter baos = new OutputStreamWriter(new ByteArrayOutputStream(), Charset.forName(Constants.ENCODING).newEncoder())) {
					serializer.serializePayload(baos, testRun, true);

					LOGGER.severe("The test run in error: " + baos.toString());
				}
				catch (IOException baosIoe) {}

				if (conn != null) {
					try {
						if (conn.getErrorStream() != null) {
							LOGGER.severe("Unable to send the test run to Probe Dock. Error: " + readInputStream(conn.getErrorStream()));
						} else {
							LOGGER.log(Level.SEVERE, "Unable to send the test run to Probe Dock. This is probably due to an unreachable network issue.", ioe);
						}
					} catch (IOException errorIoe) {
						LOGGER.severe("Unable to send the test run to Probe Dock for unknown reason.");
					}
				} else {
					LOGGER.log(Level.SEVERE, "Unable to send the test run to Probe Dock.", ioe);
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

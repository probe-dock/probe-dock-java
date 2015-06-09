package io.probedock.client.common.config;

import java.util.Map;

/**
 * Proxy configuration.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ProxyConfiguration {
	private String host;
	private int port;

	public void configureWith(Map<String, Object> data) {
		this.host = configureString(host, data, "host");
		this.port = configureInt(port, data, "port");
	}

	public boolean isValid() {
		return port > 0 && port < 65536 && host != null && !host.isEmpty();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	private String configureString(String previousValue, Map<String, Object> data, String key) {
		final Object value = data.get(key);
		return value != null ? value.toString() : previousValue;
	}

	private int configureInt(int previousValue, Map<String, Object> data, String key) {
		if (data.get(key) instanceof Integer) {
			final Integer value = (Integer) data.get(key);
			return value > 0 && value < 65536 ? value : previousValue;
		}
		else {
			throw new ProbeConfigurationException("The " + key + "[" + data.get(key) + "] is not an integer.");
		}
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("host: \"").append(host).append("\"");
		builder.append(", port: \"").append(port).append("\"");

		return builder.toString();
	}
}

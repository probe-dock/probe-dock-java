package io.probedock.client.common.config;

import io.probedock.client.common.utils.FingerprintGenerator;

import java.util.Map;

/**
 * Probe Dock server configuration.
 *
 * @author Simon Oulevay simon.oulevay@probedock.io
 */
public class ServerConfiguration {

	private String name;
	private String apiUrl;
	private String apiToken;
	private String projectApiId;
	private ProxyConfiguration proxyConfiguration;
	
	public ServerConfiguration(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public void configureWith(Map<String, Object> data) {
		this.apiUrl = configureString(apiUrl, data, "apiUrl");
		this.apiToken = configureString(apiToken, data, "apiToken");
		this.projectApiId = configureString(projectApiId, data, "projectApiId");

		if (data.containsKey("proxy")) {
			this.proxyConfiguration = new ProxyConfiguration();
			if (data.get("proxy") instanceof Map) {
				this.proxyConfiguration.configureWith((Map) data.get("proxy"));
			}
			else {
				throw new ProbeConfigurationException("Unable to parse the proxy configuration for server: " + name);
			}
		}
	}

	public String getBaseUrlFootprint() {
		return FingerprintGenerator.fingerprint(apiUrl);
	}

	public boolean isValid() {
		return apiUrl != null && apiToken != null;
	}

	public String getName() {
		return name;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public String getApiToken() {
		return apiToken;
	}

	public String getProjectApiId() {
		return projectApiId;
	}

	public boolean hasProxyConfiguration() {
		return proxyConfiguration != null;
	}
	
	public ProxyConfiguration getProxyConfiguration() {
		return proxyConfiguration;
	}

	private String configureString(String previousValue, Map<String, Object> data, String key) {
		final Object value = data.get(key);
		return value != null ? value.toString() : previousValue;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("name: \"").append(name).append("\"");
		builder.append(", apiUrl: \"").append(apiUrl).append("\"");
		builder.append(", apiToken: \"").append(apiToken).append("\"");

		builder.append(", projectApiId: \"").append(projectApiId).append("\"");
		
		if (proxyConfiguration != null) {
			builder.append(", proxy: \"").append(proxyConfiguration).append("\"");
		}

		return builder.toString();
	}
}

package io.probedock.client.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Probe Dock server list.
 *
 * @author Simon Oulevay <simon.oulevay@probe-dock.io>
 */
public class ServerListConfiguration {

	private Map<String, ServerConfiguration> servers;

	public ServerListConfiguration() {
		servers = new HashMap<>();
	}

	public void configureServer(String name, Map<String, Object> data) {
		ServerConfiguration server = servers.get(name);

		if (server == null) {
			server = new ServerConfiguration(name);
			servers.put(name, server);
		}

		server.configureWith(data);
	}

	public ServerConfiguration get(String name) {
		return servers.get(name);
	}

	public boolean isEmpty() {
		return servers.isEmpty();
	}

	public Set<String> getServerNames() {
		return Collections.unmodifiableSet(servers.keySet());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("Server list:");

		for (final ServerConfiguration server : servers.values()) {
			builder.append("\n- ").append(server);
		}

		return builder.toString();
	}
}

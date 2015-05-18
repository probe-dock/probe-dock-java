package io.probedock.client.common.config;

/**
 * @author Simon Oulevay <simon.oulevay@probe-dock.io>
 */
public class ProbeConfigurationException extends ProbeRuntimeException {
	public ProbeConfigurationException(String message) {
		super(message);
	}
	public ProbeConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}

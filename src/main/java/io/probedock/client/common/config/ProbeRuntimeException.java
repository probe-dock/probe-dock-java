package io.probedock.client.common.config;

/**
 * @author Simon Oulevay <simon.oulevay@probe-dock.io>
 */
public class ProbeRuntimeException extends RuntimeException {
	public ProbeRuntimeException(String message) {
		super(message);
	}
	public ProbeRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}

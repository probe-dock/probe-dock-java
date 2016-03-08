package io.probedock.client.common.config;

import io.probedock.client.ProbeRuntimeException;

/**
 * @author Simon Oulevay simon.oulevay@probedock.io
 */
public class ProbeConfigurationException extends ProbeRuntimeException {
	public ProbeConfigurationException(String message) {
		super(message);
	}
}

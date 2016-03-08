package io.probedock.client.common.model;

/**
 * Info concerning the probe itself
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public interface ProbeInfo {
    /**
     * @return Name of the probe (Ex: JUnit)
     */
    String getName();

    /**
     * @return Version of the probe
     */
    String getVersion();
}

package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.probedock.client.common.model.ProbeInfo;

/**
 * Probe info
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class Probe implements ProbeInfo {
    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @Override
    public String getName() {
        return name;
    }

    public Probe() {}

    public Probe(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return
            "Name: " + name + ", " +
            "Version: " + version;
    }
}

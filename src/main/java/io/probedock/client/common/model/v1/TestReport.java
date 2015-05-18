package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.probedock.client.common.model.ProbeTestReport;

/**
 * Define a report configuration
 *
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class TestReport implements ProbeTestReport {
    @JsonProperty("uid")
    private String uid;

    public TestReport() {}

    public TestReport(String uid) {
        this.uid = uid;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return
            "TestReport[" +
                "Uid: " + uid +
            "]";
    }
}

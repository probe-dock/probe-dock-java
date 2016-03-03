package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import io.probedock.client.common.model.ProbeContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Context of the tests execution
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class Context implements ProbeContext {
    @JsonProperty("context")
    private Map<String, Object> data = new HashMap<>();

    @Override
    public Object getProperty(String name) {
        return data.get(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public Object getPreProperty(String name) {
        return getProperty("pre." + name);
    }

    @Override
    public void setPreProperty(String name, Object value) {
        setProperty("pre." + name, value);
    }

    @Override
    public Object getPostProperty(String name) {
        return getProperty("post." + name);
    }

    @Override
    public void setPostProperty(String name, Object value) {
        setProperty("post." + name, value);
    }

    @Override
    public String toString() {
        StringBuilder sbData = new StringBuilder();

        for (Map.Entry<String, Object> e : data.entrySet()) {
            sbData.append("Entry[" + e.getKey() + ": " + e.getValue() + "], ");
        }

        return "Context: [ " + sbData.toString().replaceAll(", $", " ]");
    }
}

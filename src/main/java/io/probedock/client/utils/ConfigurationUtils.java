package io.probedock.client.utils;

import java.util.Map;

/**
 * Utilities to build the configuration from YML file
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class ConfigurationUtils {
    public static String configureString(String previousValue, Map<String, Object> data, String key) {
        final Object value = data.get(key);

        if (value == null) {
            return previousValue;
        }
        else {
            return value.toString();
        }
    }

    public static Integer configureInteger(Integer previousValue, Map<String, Object> data, String key) {
        final Object value = data.get(key);

        if (value == null) {
            return previousValue;
        }
        else {
            return Integer.parseInt(value.toString());
        }
    }

    public static Boolean configureBoolean(Boolean previousValue, Map<String, Object> data, String key) {
        final Object value = data.get(key);

        if (value == null) {
            return previousValue;
        }
        else {
            return Boolean.parseBoolean(value.toString());
        }
    }
}

package io.probedock.client.utils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class EnvironmentUtils {
    public static final Pattern BOOLEAN_PATTERN = Pattern.compile("\\A(1|y|yes|t|true)\\Z", Pattern.CASE_INSENSITIVE);

    public static final String ENV_PREFIX = "PROBEDOCK_";

    private static Map<String, String> envVars;

    /**
     * Allow to set the environment variables
     *
     * @param envVars The environment variables
     */
    public static void setEnvVars(Map<String, String> envVars) {
        EnvironmentUtils.envVars = envVars;
    }

    /**
     * Retrieve boolean value for the environment variable name
     *
     * @param name The name of the variable without prefix
     * @param defaultValue The default value if not found
     * @return The value found, or the default if not found
     */
    public static Boolean getEnvironmentBoolean(String name, Boolean defaultValue) {
        if (envVars == null) {
            throw new IllegalStateException("The environment vars must be provided before calling getEnvironmentBoolean.");
        }

        String value = getEnvironmentString(name, null);

        if (value == null) {
            return defaultValue;
        }
        else {
            return BOOLEAN_PATTERN.matcher(value).matches();
        }
    }

    /**
     * Retrieve integer value for the environment variable name
     *
     * @param name The name of the variable without prefix
     * @param defaultValue The default value if not found
     * @return The value found, or the default if not found
     */
    public static Integer getEnvironmentInteger(String name, Integer defaultValue) {
        if (envVars == null) {
            throw new IllegalStateException("The environment vars must be provided before calling getEnvironmentInteger.");
        }

        String value = getEnvironmentString(name, null);

        if (value == null) {
            return defaultValue;
        }
        else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Retrieve string value for the environment variable name
     *
     * @param name The name of the variable without prefix
     * @param defaultValue The default value if not found
     * @return The value found, or the default if not found
     */
    public static String getEnvironmentString(String name, String defaultValue) {
        if (envVars == null) {
            throw new IllegalStateException("The environment vars must be provided before calling getEnvironmentString.");
        }

        return envVars.get(ENV_PREFIX + name) != null ? envVars.get(ENV_PREFIX + name) : defaultValue;
    }
}

package io.probedock.client.utils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class EnvironmentUtilsTest {
    private static Map<String, String> envVars = new HashMap<>();

    @BeforeClass
    public static void globalSetup() {
        EnvironmentUtils.setEnvVars(envVars);
    }

    @Before
    public void setup() {
        envVars.clear();
    }

    @Test
    public void itShouldBePossibleToRetrieveStringEnvVar() {
        envVars.put("PROBEDOCK_MY_VAR", "value");
        assertEquals("value", EnvironmentUtils.getEnvironmentString("MY_VAR", "defaultValue"));
    }

    @Test
    public void itShouldBePossibleToRetrieveStringDefaultValueWhenEnvVarIsNotPresent() {
        assertEquals("defaultValue", EnvironmentUtils.getEnvironmentString("MY_VAR", "defaultValue"));
    }

    @Test
    public void itShouldBePossibleToRetrieveStringDefaultValueWhenEnvVarIsNull() {
        envVars.put("PROBEDOCK_MY_VAR", null);
        assertEquals("defaultValue", EnvironmentUtils.getEnvironmentString("MY_VAR", "defaultValue"));
        assertNull(EnvironmentUtils.getEnvironmentString("MY_VAR", null));
    }

    @Test
    public void itShouldBePossibleToRetrieveIntegerEnvVar() {
        envVars.put("PROBEDOCK_MY_VAR", "12");
        assertEquals(12, EnvironmentUtils.getEnvironmentInteger("MY_VAR", 123));
    }

    @Test
    public void itShouldBePossibleToRetrieveIntegerDefaultValueWhenEnvVarIsNotPresent() {
        assertEquals(123, EnvironmentUtils.getEnvironmentInteger("MY_VAR", 123));
    }

    @Test
    public void itShouldBePossibleToRetrieveIntegerDefaultValueWhenEnvVarIsNull() {
        envVars.put("PROBEDOCK_MY_VAR", null);
        assertEquals(123, EnvironmentUtils.getEnvironmentInteger("MY_VAR", 123));
    }

    @Test
    public void itShouldBePossibleToRetrieveBooleanEnvVar() {
        envVars.put("PROBEDOCK_MY_VAR", "false");
        assertFalse(EnvironmentUtils.getEnvironmentBoolean("MY_VAR", true));
    }

    @Test
    public void itShouldBePossibleToRetrieveBooleanDefaultValueWhenEnvVarIsNotPresent() {
        assertTrue(EnvironmentUtils.getEnvironmentBoolean("MY_VAR", true));
    }

    @Test
    public void itShouldBePossibleToRetrieveBooleanDefaultValueWhenEnvVarIsNull() {
        envVars.put("PROBEDOCK_MY_VAR", null);
        assertTrue(EnvironmentUtils.getEnvironmentBoolean("MY_VAR", true));
    }
}

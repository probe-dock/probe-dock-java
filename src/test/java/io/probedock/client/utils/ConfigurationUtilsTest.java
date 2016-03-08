package io.probedock.client.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class ConfigurationUtilsTest {
    @Test
    public void itShouldBePossibleToRetrieveStringValueWhenPresentInTheMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", "value");
        assertEquals("value", ConfigurationUtils.configureString(null, config, "key"));
    }

    @Test
    public void itShouldBePossibleToRetrieveStringValueWhenPresentInTheMapAndOverridePreviousOne() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", "value");
        assertEquals("value", ConfigurationUtils.configureString("oldValue", config, "key"));
    }

    @Test
    public void itShouldReturnPreviousStringValueWhenNoNewValueIsPresent() {
        Map<String, Object> config = new HashMap<>();
        assertEquals("oldValue", ConfigurationUtils.configureString("oldValue", config, "key"));
        assertNull(ConfigurationUtils.configureString(null, config, "key"));
    }

    @Test
    public void itShouldBePossibleToRetrieveBooleanValueWhenPresentInTheMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", true);
        assertTrue(ConfigurationUtils.configureBoolean(null, config, "key"));
    }

    @Test
    public void itShouldBePossibleToRetrieveBooleanValueWhenPresentInTheMapAndOverridePreviousOne() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", false);
        assertFalse(ConfigurationUtils.configureBoolean(true, config, "key"));
    }

    @Test
    public void itShouldReturnPreviousBooleanValueWhenNoNewValueIsPresent() {
        Map<String, Object> config = new HashMap<>();
        assertTrue(ConfigurationUtils.configureBoolean(true, config, "key"));
        assertNull(ConfigurationUtils.configureBoolean(null, config, "key"));
    }

    @Test
    public void itShouldBePossibleToRetrieveIntegerValueWhenPresentInTheMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", 3);
        assertEquals(new Integer(3), ConfigurationUtils.configureInteger(null, config, "key"));
    }

    @Test
    public void itShouldBePossibleToRetrieveIntegerValueWhenPresentInTheMapAndOverridePreviousOne() {
        Map<String, Object> config = new HashMap<>();
        config.put("key", 3);
        assertEquals(new Integer(3), ConfigurationUtils.configureInteger(5, config, "key"));
    }

    @Test
    public void itShouldReturnPreviousIntegerValueWhenNoNewValueIsPresent() {
        Map<String, Object> config = new HashMap<>();
        assertEquals(new Integer(3), ConfigurationUtils.configureInteger(3, config, "key"));
        assertNull(ConfigurationUtils.configureInteger(null, config, "key"));
    }
}


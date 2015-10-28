package io.probedock.client.common.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test for class {@link PackageMatcher}
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class PackageMatcherTest {
    @Test
    public void itShouldMatchPackageThroughMinimatchPattern() {
        Map<String, String> m = new HashMap<>();

        m.put("io.probedock", "category");

        assertNotNull(PackageMatcher.match(m, "io.probedock"));

        m.put("io.probedock.test.integration.*", "integration");

        assertEquals(PackageMatcher.match(m, "io.probedock.test.integration.user").getValue(), "integration");
        assertNull(PackageMatcher.match(m, "com.google"));
    }
}

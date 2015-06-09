package io.probedock.client.common.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test for class {@link io.probedock.client.common.utils.FingerprintGenerator}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class FingerprintGeneratorTest {
	@Test
	public void generationOfFingerpringFromStringShouldBeCorrect() {
		assertEquals("b5448ce070e8ff567c4870e9fe0aeba3c0a98330", FingerprintGenerator.fingerprint("fingerprint"));
	}

	@Test
	public void generationOfFingerpringFromPackageClassAndMethodShouldBeCorrect() {
		assertEquals("5c72161022fb8bde99c23bcef6bac287f153dfce", FingerprintGenerator.fingerprint("package", "class", "method"));
	}
}

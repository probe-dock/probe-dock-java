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
	public void generationOfFingerprintFromStringShouldBeCorrect() {
		assertEquals("b5448ce070e8ff567c4870e9fe0aeba3c0a98330", FingerprintGenerator.fingerprint("fingerprint"));
	}

	@Test
	public void generationOfFingerprintFromClassAndMethodShouldBeCorrect() throws Exception {
		assertEquals("edb004bc7e7d1261c9e2f049a81a1c76481258a4", FingerprintGenerator.fingerprint(this.getClass(), this.getClass().getDeclaredMethod("generationOfFingerprintFromClassAndMethodShouldBeCorrect")));
	}

	@Test
	public void generationOfFingerprintFromClassAndMethodNameShouldBeCorrect() throws Exception {
		assertEquals("c39281690d1db17985e59b3a25c2b3b7e58aff87", FingerprintGenerator.fingerprint(this.getClass(), "generationOfFingerprintFromClassAndMethodNameShouldBeCorrect"));
	}
}

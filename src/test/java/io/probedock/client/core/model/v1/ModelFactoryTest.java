package io.probedock.client.core.model.v1;

import io.probedock.client.common.model.v1.ModelFactory;
import io.probedock.client.common.model.v1.TestResult;
import io.probedock.client.common.model.v1.TestRun;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * TestResult for claas {@link ModelFactory}
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class ModelFactoryTest {
	private TestResult validTestResult;

	@Before
	public void setUp() {
		Map<String, String> data = new HashMap<>();
		data.put("key", "value");
	
		validTestResult = ModelFactory.createTestResult("key", "name", "category",
			10L, "message", true, false,
			new HashSet<>(Arrays.asList(new String[]{"tag"})),
			new HashSet<>(Arrays.asList(new String[]{"ticket"})),
			data
		);
	}

	@Test
	public void testCreationWithAllAttributesShouldBePossible() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("key", "value");

		TestResult testResult =
			ModelFactory.createTestResult("key", "name", "category",
				10L, "message", true, false,
				new HashSet<>(Arrays.asList(new String[]{"tag"})),
				new HashSet<>(Arrays.asList(new String[]{"ticket"})),
				data
			);

		assertNotNull("The testResult was not created", testResult);
	}
	
	@Test
	public void testCreationWithOnlyMandatoryAttributesShouldBePossible() {
		TestResult testResult = ModelFactory.createTestResult("key", null, null, 0L, null, true, true, null, null, null);

		assertNotNull("The testResult was not created", testResult);
	}
	
	@Test
	public void testCreationWithMissingMandatoryAttributes() {
		try {
			ModelFactory.createTestResult(null, null, null, 0L, null, true, false, null, null, null);
			fail("The test should not be created without a key");
		}
		catch (IllegalArgumentException iae) {}
	}
	
	@Test
	public void testFailedWithEmptyMessageShouldBeFilledWithDefaultMessage() {
		TestResult testResult = ModelFactory.createTestResult("key", null, null, 0L, "", false, false, null, null, null);
		assertEquals("No content for the message provided for the failing testResult", testResult.getMessage());
	}
	
	@Test
	public void testFailedWithNullMessageShouldBeFilledWithDefaultMessage() {
		TestResult testResult = ModelFactory.createTestResult("key", null, null, 0L, null, false, false, null, null, null);
		assertEquals("No message provided for the failing testResult", testResult.getMessage());
	}
	@Test
	public void testCreationWithThePlaceholderForStatusShouldBeSilentlyIgnored() {
		TestResult testResult = ModelFactory.createTestResult("key", null, null, 0L, null, true, false, null, null, null);
			
		assertNotNull("A placeholder status should be silently ignored and the testResult should be created", testResult);
	}

	@Test
	public void testCreationWithNegativeDurationIsNotPossible() {
		try {
			ModelFactory.createTestResult("key", null, null, -10L, null, false, false, null, null, null);
			
			fail("The test should not be created with a negative duration");
		}
		catch (IllegalArgumentException iae) {}
	}
	
	@Test
	public void testRunCreationWithAllAttributesShouldBePossible() {
		TestRun testRun = ModelFactory.createTestRun("project", "version", 10L, Arrays.asList(validTestResult), null);

		assertNotNull("The test run was not created", testRun);
	}
	
	@Test
	public void testRunCreationWithOnlyMandatoryAttributesShouldBePossible() {
		TestRun testRun = ModelFactory.createTestRun(null, null, 10L, Arrays.asList(validTestResult), null);

		assertNotNull("The test run was not created", testRun);
	}
	
	@Test
	public void testRunCreationWithoutAtLeastOneTest() {
		try {
			ModelFactory.createTestRun(null, null, 10L, Arrays.asList(new TestResult[] { }), null);

			fail("The test run should not be created without at least one test");
		}
		catch (IllegalArgumentException iae) {}
	}

	@Test
	public void testRunCreationWithNegativeDurationIsNotPossible() {
		try {
			ModelFactory.createTestRun(null, null, -10L, Arrays.asList(new TestResult[] { }), null);

			fail("The test run should not be created with negative duration");
		}
		catch (IllegalArgumentException iae) {}
	}
}

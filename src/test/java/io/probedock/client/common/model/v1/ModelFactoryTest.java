package io.probedock.client.common.model.v1;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.utils.MetaDataBuilder;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for class {@link ModelFactory}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ModelFactoryTest {
	private TestResult validTestResult;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		validTestResult = ModelFactory.createTestResult(
			"key",
			"fingerprint",
			"name",
			"category",
			10L,
			"message",
			true,
			false,
			new HashSet<>(Arrays.asList(new String[]{"contributor"})),
			new HashSet<>(Arrays.asList(new String[]{"tag"})),
			new HashSet<>(Arrays.asList(new String[]{"ticket"})),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);
	}

	@Test
	public void fingerprintShouldBeBasedOnPackageClassAndMethodNames() throws Exception {
		final String result = ModelFactory.createFingerprint(this.getClass(), this.getClass().getDeclaredMethod("fingerprintShouldBeBasedOnPackageClassAndMethodNames"));

		assertEquals("Fingerprints must be the same", "2124ed3c55b62e67bb3d00b79324d6094d47ec34", result);
	}

	@Test
	public void createContextShouldReturnFilledContextWithSeveralJavaAndPlatformInfo() {
		Context context = ModelFactory.createContext();

		assertNotNull(context.getProperty(Context.OS_NAME));
		assertNotNull(context.getProperty(Context.OS_VERSION));
		assertNotNull(context.getProperty(Context.OS_ARCHITECTURE));

		assertNotNull(context.getProperty(Context.JAVA_VERSION));
		assertNotNull(context.getProperty(Context.JAVA_VENDOR));

		assertNotNull(context.getProperty(Context.JAVA_RUNTIME_NAME));
		assertNotNull(context.getProperty(Context.JAVA_RUNTIME_VERSION));

		assertNotNull(context.getProperty(Context.JAVA_VM_NAME));
		assertNotNull(context.getProperty(Context.JAVA_VM_VERSION));
		assertNotNull(context.getProperty(Context.JAVA_VM_VENDOR));

		assertNotNull(context.getProperty(Context.JAVA_VM_SPEC_NAME));
		assertNotNull(context.getProperty(Context.JAVA_VM_SPEC_VERSION));
		assertNotNull(context.getProperty(Context.JAVA_VM_VENDOR));

		assertNotNull(context.getProperty(Context.JAVA_SPEC_NAME));
		assertNotNull(context.getProperty(Context.JAVA_SPEC_VERSION));
		assertNotNull(context.getProperty(Context.JAVA_SPEC_VENDOR));

		assertNotNull(context.getProperty(Context.JAVA_CLASS_VERSION));

		assertNotNull(context.getPreProperty(Context.MEMORY_TOTAL));
		assertNotNull(context.getPreProperty(Context.MEMORY_FREE));
		assertNotNull(context.getPreProperty(Context.MEMORY_USED));
	}

	@Test
	public void enrichContextShouldAddSeveralJavaAndPlatformInfo() {
		Context context = new Context();

		assertNull(context.getPostProperty(Context.MEMORY_TOTAL));
		assertNull(context.getPostProperty(Context.MEMORY_FREE));
		assertNull(context.getPostProperty(Context.MEMORY_USED));

		ModelFactory.enrichContext(context);

		assertNotNull(context.getPostProperty(Context.MEMORY_TOTAL));
		assertNotNull(context.getPostProperty(Context.MEMORY_FREE));
		assertNotNull(context.getPostProperty(Context.MEMORY_USED));
	}

	@Test
	public void createProbeShouldSetTheNameAndVersionOfTheProbe() {
		Probe probe = ModelFactory.createProbe("name", "version");

		assertEquals("name", probe.getName());
		assertEquals("version", probe.getVersion());
	}

	@Test
	public void testRunCreationWithAllAttributesShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestReport testReport = new TestReport();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"stage",
			Arrays.asList(testReport),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNotNull(testRun);
		assertEquals("v1", testRun.getApiVersion());
		assertEquals(context, testRun.getContext());
		assertEquals(probe, testRun.getProbe());
		assertEquals("projectId", testRun.getProjectId());
		assertEquals("version", testRun.getVersion());
		assertEquals("pipeline", testRun.getPipeline());
		assertEquals("stage", testRun.getStage());
		assertFalse(testRun.getTestReports().isEmpty());
		assertEquals(testReport, testRun.getTestReports().get(0));
		assertTrue(testRun.getTestResults().isEmpty());
		assertNotNull(testRun.getData());
		assertEquals("value", testRun.getData().get("key"));
	}

	@Test
	public void testRunCreationWithoutPipelineShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestReport testReport = new TestReport();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			null,
			"stage",
			Arrays.asList(testReport),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNull(testRun.getPipeline());

		testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"",
			"stage",
			Arrays.asList(testReport),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNull(testRun.getPipeline());
	}

	@Test
	public void testRunCreationWithoutStageShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestReport testReport = new TestReport();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			null,
			Arrays.asList(testReport),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNull(testRun.getStage());

		testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"",
			Arrays.asList(testReport),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNull(testRun.getStage());
	}

	@Test
	public void testRunCreationWithoutReportShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestReport testReport = new TestReport();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"stage",
			null,
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNotNull(testRun.getTestReports());
		assertTrue(testRun.getTestReports().isEmpty());

		testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"stage",
			new ArrayList<TestReport>(),
			new MetaDataBuilder().add("key", "value").toMetaData()
		);

		assertNotNull(testRun.getTestReports());
		assertTrue(testRun.getTestReports().isEmpty());
	}

	@Test
	public void testRunCreationWithoutDataShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestReport testReport = new TestReport();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"stage",
			null,
			null
		);

		assertNull(testRun.getData());

		testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			"pipeline",
			"stage",
			new ArrayList<TestReport>(),
			new HashMap<String, String>()
		);

		assertNull(testRun.getData());
	}

	@Test
	public void testRunCreationWithOnlyMandatoryAttributesShouldBePossible() {
		Context context = new Context();
		Probe probe = new Probe();

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			context,
			probe,
			"projectId",
			"version",
			null,
			null,
			null,
			null
		);

		assertNotNull(testRun);
		assertEquals("v1", testRun.getApiVersion());
		assertEquals(context, testRun.getContext());
		assertEquals(probe, testRun.getProbe());
		assertEquals("projectId", testRun.getProjectId());
		assertEquals("version", testRun.getVersion());
		assertNull(testRun.getPipeline());
		assertNull(testRun.getStage());
		assertTrue(testRun.getTestReports().isEmpty());
		assertTrue(testRun.getTestResults().isEmpty());
		assertNull(testRun.getData());
	}

	@Test
	public void testRunCreationWithoutContextShouldRaiseAnIllegalArgumentException() {
		try {
			ModelFactory.createTestRun(
				configuration,
				null,
				new Probe(),
				"projectId",
				"version",
				null,
				null,
				null,
				null
			);

			fail("Test run creation without context should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("Execution context must be present.", iae.getMessage());
		}
	}

	@Test
	public void testRunCreationWithoutProbeShouldRaiseAnIllegalArgumentException() {
		try {
			ModelFactory.createTestRun(
				configuration,
				new Context(),
				null,
				"projectId",
				"version",
				null,
				null,
				null,
				null
			);

			fail("Test run creation without probe should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("Probe info must be present.", iae.getMessage());
		}
	}

	@Test
	public void testRunCreationWithoutProjectIdShouldRaiseAnIllegalArgumentException() {
		try {
			ModelFactory.createTestRun(
				configuration,
				new Context(),
				new Probe(),
				null,
				"version",
				null,
				null,
				null,
				null
			);

			fail("Test run creation without project ID should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The project ID must be present.", iae.getMessage());
		}

		try {
			ModelFactory.createTestRun(
				configuration,
				new Context(),
				new Probe(),
				"",
				"version",
				null,
				null,
				null,
				null
			);

			fail("Test run creation without project ID should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The project ID must be present.", iae.getMessage());
		}
	}

	@Test
	public void testRunCreationWithoutVersionShouldRaiseAnIllegalArgumentException() {
		try {
			ModelFactory.createTestRun(
				configuration,
				new Context(),
				new Probe(),
				"projectId",
				null,
				null,
				null,
				null,
				null
			);

			fail("Test run creation without version should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The project version must be present.", iae.getMessage());
		}

		try {
			ModelFactory.createTestRun(
				configuration,
				new Context(),
				new Probe(),
				"projectId",
				"",
				null,
				null,
				null,
				null
			);

			fail("Test run creation without version should raise an IllegalStateException.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The project version must be present.", iae.getMessage());
		}
	}

	@Test
	public void testRunCreationShouldSetTheDefaultReportIfUidIsPresent() {
		when(configuration.getCurrentUid()).thenReturn("uid");

		TestRun testRun = ModelFactory.createTestRun(
			configuration,
			new Context(),
			new Probe(),
			"projectId",
			"version",
			"pipeline",
			"stage",
			null,
			null
		);

		assertEquals(1, testRun.getTestReports().size());
		assertEquals("uid", testRun.getTestReports().get(0).getUid());
		assertEquals(1, testRun.getData().size());
		assertEquals("uid", testRun.getData().get("probedock.report.uid"));
	}

	@Test
	public void testCreationWithAllAttributesShouldBePossible() {
		TestResult testResult =
			ModelFactory.createTestResult(
				"key",
				"fingerprint",
				"name",
				"category",
				10L,
				"message",
				true,
				false,
				new HashSet<>(Arrays.asList(new String[]{"contributor"})),
				new HashSet<>(Arrays.asList(new String[]{"tag"})),
				new HashSet<>(Arrays.asList(new String[]{"ticket"})),
				new MetaDataBuilder().add("key", "value").toMetaData()
			);

		assertNotNull(testResult);
		assertEquals("key", testResult.getKey());
		assertEquals("fingerprint", testResult.getFingerprint());
		assertEquals("name", testResult.getName());
		assertEquals("category", testResult.getCategory());
		assertEquals(10L, testResult.getDuration());
		assertEquals("contributor", testResult.getContributors().toArray()[0]);
		assertEquals("tag", testResult.getTags().toArray()[0]);
		assertEquals("ticket", testResult.getTickets().toArray()[0]);
		assertEquals("value", testResult.getData().get("key"));
	}

	@Test
	public void testCreationWithOnlyMandatoryAttributesShouldBePossible() {
		TestResult testResult = ModelFactory.createTestResult(
			null,
			"fingerprint",
			null,
			null,
			0L,
			null,
			true,
			true,
			null,
			null,
			null,
			null
		);

		assertNotNull("The testResult was not created", testResult);
	}

	@Test
	public void testCreationWithoutFingerprintShouldRaiseAnError() {
		try {
			ModelFactory.createTestResult(
				null,
				null, // Null fingerprint (mandatory)
				null,
				null,
				0L,
				null,
				true,
				true,
				null,
				null,
				null,
				null
			);

			fail("It should not be possible to create a test result without a fingerprint.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The fingerprint is mandatory.", iae.getMessage());
		}
	}

	@Test
	public void testCreationWithNegativeDurationShouldRaiseAnError() {
		try {
			ModelFactory.createTestResult(
				null,
				null, // Null fingerprint (mandatory)
				null,
				null,
				-1L,
				null,
				true,
				true,
				null,
				null,
				null,
				null
			);

			fail("It should not be possible to create a test result with a negative duration.");
		}
		catch (IllegalArgumentException iae) {
			assertEquals("The duration cannot be negative.", iae.getMessage());
		}
	}

	@Test
	public void testCreationShouldSetDefaultMessageWhenTestIsFailedAndNullMessageWasProvided() {
		TestResult testResult = ModelFactory.createTestResult(
			"key",
			"fingerprint",
			null,
			null,
			0L,
			null,
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertEquals("No message available.", testResult.getMessage());
	}

	@Test
	public void testCreationShouldSetDefaultMessageWhenTestIsFailedAndEmptyMessageWasProvided() {
		TestResult testResult = ModelFactory.createTestResult(
			"key",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertEquals("Failing message was empty.", testResult.getMessage());
	}

	@Test
	public void testCreationShouldSetTheKeyOnlyIfNotNullOrNotEmpty() {
		TestResult testResult = ModelFactory.createTestResult(
			"",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.getKey());

		testResult = ModelFactory.createTestResult(
			null,
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.getKey());
	}

	@Test
	public void testCreationShouldTruncateTheMessageWhenBiggerThan50kCaracters() {
		TestResult testResult = ModelFactory.createTestResult(
			null,
			"fingerprint",
			null,
			null,
			0L,
			StringUtils.leftPad("", 60000, '*'),
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertEquals(StringUtils.leftPad("...", 50000, '*'), testResult.getMessage());
	}

	@Test
	public void testCreationShouldSetTheCategoryOnlyIfNotNullOrNotEmpty() {
		TestResult testResult = ModelFactory.createTestResult(
			"",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.getCategory());

		testResult = ModelFactory.createTestResult(
			null,
			"fingerprint",
			null,
			"",
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.getCategory());
	}

	@Test
	public void testCreationShouldSetTheActiveOnlyIfNotNull() {
		TestResult testResult = ModelFactory.createTestResult(
			"",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			null,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.isActive());
	}

	@Test
	public void testCreationShouldSetTheContributorsTagsTicketsAndDataOnlyIfTheyAreNotNull() {
		TestResult testResult = ModelFactory.createTestResult(
			"",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			null,
			null,
			null,
			null
		);

		assertNull(testResult.getContributors());
		assertNull(testResult.getTags());
		assertNull(testResult.getTickets());
		assertNotNull(testResult.getData());
		assertEquals(1, testResult.getData().size());
		assertNotNull(testResult.getData().get("fingerprint"));

		testResult = ModelFactory.createTestResult(
			"",
			"fingerprint",
			null,
			null,
			0L,
			"",
			false,
			false,
			new HashSet<String>(),
			new HashSet<String>(),
			new HashSet<String>(),
			new HashMap<String, String>()
		);

		assertTrue(testResult.getContributors().isEmpty());
		assertTrue(testResult.getTags().isEmpty());
		assertTrue(testResult.getTickets().isEmpty());
		assertFalse(testResult.getData().isEmpty());
		assertEquals(1, testResult.getData().size());
		assertNotNull(testResult.getData().get("fingerprint"));
	}


	@Test
	public void enrichTestResultShouldAddPackageClassAndMethodNamesInMetaData() {
		TestResult testResult = new TestResult();

		ModelFactory.enrichTestResult(testResult, "package", "class", "method");

		assertEquals("package", testResult.getData().get("java.package"));
		assertEquals("class", testResult.getData().get("java.class"));
		assertEquals("method", testResult.getData().get("java.method"));
	}

	@Test
	public void createTestReportShouldBePossible() {
		TestReport testReport = ModelFactory.createTestReport("uid");

		assertEquals("uid", testReport.getUid());
	}
}

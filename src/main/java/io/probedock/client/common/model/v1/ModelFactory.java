package io.probedock.client.common.model.v1;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.common.utils.Constants;
import io.probedock.client.common.utils.FingerprintGenerator;
import io.probedock.client.common.utils.MetaDataBuilder;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model factory to facilitate the creation of tests, test runs
 * and payloads.
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ModelFactory {
	/**
	 * Create fingerprint for java test method
	 *
	 * @param cl The test class
	 * @param m The test method
	 * @return The fingerprint generated
	 */
	public static String createFingerprint(Class cl, Method m) {
		return FingerprintGenerator.fingerprint(cl, m);
	}

	/**
	 * Create a context
	 *
	 * @return The context created
	 */
	public static Context createContext() {
		Context context = new Context();

		context.setProperty(Context.OS_NAME, System.getProperty("os.name"));
		context.setProperty(Context.OS_VERSION, System.getProperty("os.version"));
		context.setProperty(Context.OS_ARCHITECTURE, System.getProperty("os.arch"));

		context.setProperty(Context.JAVA_VERSION, System.getProperty("java.version"));
		context.setProperty(Context.JAVA_VENDOR, System.getProperty("java.vendor"));

		context.setProperty(Context.JAVA_RUNTIME_NAME, System.getProperty("java.runtime.name"));
		context.setProperty(Context.JAVA_RUNTIME_VERSION, System.getProperty("java.runtime.version"));

		context.setProperty(Context.JAVA_VM_NAME, System.getProperty("java.vm.name"));
		context.setProperty(Context.JAVA_VM_VERSION, System.getProperty("java.vm.version"));
		context.setProperty(Context.JAVA_VM_VENDOR, System.getProperty("java.vm.vendor"));

		context.setProperty(Context.JAVA_VM_SPEC_NAME, System.getProperty("java.vm.specification.name"));
		context.setProperty(Context.JAVA_VM_SPEC_VERSION, System.getProperty("java.vm.specification.version"));
		context.setProperty(Context.JAVA_VM_SPEC_VENDOR, System.getProperty("java.vm.specification.vendor"));

		context.setProperty(Context.JAVA_SPEC_NAME, System.getProperty("java.specification.name"));
		context.setProperty(Context.JAVA_SPEC_VERSION, System.getProperty("java.specification.version"));
		context.setProperty(Context.JAVA_SPEC_VENDOR, System.getProperty("java.specification.vendor"));

		context.setProperty(Context.JAVA_CLASS_VERSION, System.getProperty("java.class.version"));

		context.setPreProperty(Context.MEMORY_TOTAL, Runtime.getRuntime().totalMemory());
		context.setPreProperty(Context.MEMORY_FREE, Runtime.getRuntime().freeMemory());
		context.setPreProperty(Context.MEMORY_USED, Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		return context;
	}

	/**
	 * Enrich a context
	 */
	public static void enrichContext(Context context) {
		context.setPostProperty(Context.MEMORY_TOTAL, Runtime.getRuntime().totalMemory());
		context.setPostProperty(Context.MEMORY_FREE, Runtime.getRuntime().freeMemory());
		context.setPostProperty(Context.MEMORY_USED, Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}

	/**
	 * Create a probe info
	 *
	 * @param name The name of the probe
	 * @param version The version of the probe
	 * @return The probe info created
	 */
	public static Probe createProbe(String name, String version) {
		return new Probe(name, version);
	}

	/**
	 * Create a test run
	 *
	 * @param config The configuration
	 * @param context The execution context (mandatory)
	 * @param probe The probe info (mandatory)
	 * @param projectId The project API identifier (mandatory)
	 * @param version The project version (mandatory)
	 * @param pipeline The pipeline (optional)
	 * @param stage The stage in the pipeline (optional)
	 * @param testReports The testReports (optional)
	 * @param data The additional data (optional)
	 * @return The test run created
	 */
	public static TestRun createTestRun(Configuration config,
										Context context, Probe probe,
										String projectId, String version,
										String pipeline, String stage,
										List<TestReport> testReports,
										Map<String, String> data) {

		if (context == null) {
			throw new IllegalArgumentException("Execution context must be present.");
		}

		if (probe == null) {
			throw new IllegalArgumentException("Probe info must be present.");
		}

		if (version == null || version.isEmpty()) {
			throw new IllegalArgumentException("The project version must be present.");
		}

		if (projectId == null || projectId.isEmpty()) {
			throw new IllegalArgumentException("The project ID must be present.");
		}
		
		final TestRun testRun = new TestRun();

		testRun.setContext(context);
		testRun.setProbe(probe);
		testRun.setProjectId(projectId);
		testRun.setVersion(version);

		if (pipeline != null && !pipeline.isEmpty()) {
			testRun.setPipeline(pipeline);
		}

		if (stage != null && !stage.isEmpty()) {
			testRun.setStage(stage);
		}

		String uid = config.getCurrentUid();
		if (uid != null && !uid.isEmpty()) {
			testRun.getTestReports().add(new TestReport(uid));
			testRun.addData(ProbeTestRun.PROBEDOCK_REPORT_UID, uid);
		}

		if (testReports != null && !testReports.isEmpty()) {
			testRun.getTestReports().addAll(testReports);
		}

		if (data != null && !data.isEmpty()) {
			testRun.addData(data);
		}

		return testRun;
	}
	
	/**
	 * Create a test result
	 *
	 * @param key The unique key that identifies the test (mandatory)
	 * @param name Name
	 * @param duration Approximative duration time (0 or positive)
	 * @param message Message to enrich the result (mandatory when failed, max. 65535 car, truncated)
	 * @param passed Flag to know if a test pass or not
	 * @param active if the test is active
	 * @param contributors A list of contributors
	 * @param tags A list of tags
	 * @param tickets A list of tickets
	 * @param data A list of meta data
	 * @return Created test
	 */
	public static TestResult createTestResult(String key, String fingerprint, String name,
											  String category, long duration, String message,
											  boolean passed, Boolean active, Set<String> contributors,
											  Set<String> tags, Set<String> tickets, Map<String, String> data) {

		if (duration < 0) {
			throw new IllegalArgumentException("The duration cannot be negative.");
		}

		if (fingerprint == null || fingerprint.isEmpty()) {
			throw new IllegalArgumentException("The fingerprint is mandatory.");
		}

		TestResult testResult = new TestResult();

		if (!passed) {
			if (message == null) {
				testResult.setMessage("No message available.");
			}
			else if (message.isEmpty()) {
				testResult.setMessage("Failing message was empty.");
			}
		}
		
		if (key != null && !key.isEmpty()) {
			testResult.setKey(key);
		}

		testResult.setFingerprint(fingerprint);
		testResult.setName(name);
		testResult.setDuration(duration);
		testResult.setPassed(passed);
		
		if (testResult.getMessage() == null) {
			if (message != null && message.getBytes(Charset.forName(Constants.ENCODING)).length > 50000) {
				testResult.setMessage(new String(message.getBytes(Charset.forName(Constants.ENCODING)), 0, 49997, Charset.forName(Constants.ENCODING)) + "...");
			}
			else {
				testResult.setMessage(message);
			}
		}
		
		if (category != null && !category.isEmpty()) {
			testResult.setCategory(category);
		}

		if (active != null) {
			testResult.setActive(active);
		}
		
		if (tags != null) {
			testResult.addTags(tags);
		}

		if (contributors != null) {
			testResult.addContributors(contributors);
		}
		
		if (tickets != null) {
			testResult.addTickets(tickets);
		}
		
		if (data != null) {
			testResult.addData(data);
		}

		// Add the fingerprint in the data to help the migration of the server data
		testResult.addData("fingerprint", fingerprint);
		
		return testResult;
	}

	/**
	 * Enrich the test result with the java package, class and method names.
	 *
	 * @param result The result to enrich
	 * @param packageName The package name
	 * @param className The class name
	 * @param methodName The method name
	 */
	public static void enrichTestResult(TestResult result, String packageName, String className, String methodName) {
		MetaDataBuilder builder = new MetaDataBuilder();

		builder
			.add("java.package", packageName)
			.add("java.class", className)
			.add("java.method", methodName);

		result.addData(builder.toMetaData());
	}

	/**
	 * Create a test report
	 *
	 * @param uid The UID to set to the test report
	 * @return The test report created
	 */
	public static TestReport createTestReport(String uid) {
		return new TestReport(uid);
	}
}

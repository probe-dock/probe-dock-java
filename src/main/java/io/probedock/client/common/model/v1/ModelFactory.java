package io.probedock.client.common.model.v1;

import io.probedock.client.common.utils.Constants;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model factory to facilitate the creation of tests, test runs
 * and payloads.
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class ModelFactory {
	/**
	 * Create a test run
	 * 
	 * @param projectApiId The project API identifier
	 * @param projectVersion The project version
	 * @param duration The duration of the run (only 0 or positive allowed)
	 * @param testResults The testResults (at least one must be present)
	 * @param testReports The testReports (optional)
	 * @return The test run created
	 */
	public static TestRun createTestRun(String projectApiId, String projectVersion,
		long duration, List<TestResult> testResults, List<TestReport> testReports) {

		if (testResults == null || testResults.isEmpty()) {
			throw new IllegalArgumentException("At least one test must be present.");
		}
		
		if (duration < 0) {
			throw new IllegalArgumentException("The duration cannot be negative.");
		}
		
		final TestRun testRun = new TestRun();

		testRun.setProjectId(projectApiId);
		testRun.setProjectVersion(projectVersion);
		testRun.setDuration(duration);

		testRun.getTestResults().addAll(testResults);

		if (testReports != null && !testReports.isEmpty()) {
			testRun.getTestReports().addAll(testReports);
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
	 * @param tags A list of tags
	 * @param tickets A list of tickets
	 * @param data A list of meta data
	 * @return Created test
	 */
	public static TestResult createTestResult(String key, String name, String category,
											  long duration, String message, boolean passed, Boolean active,
											  Set<String> tags, Set<String> tickets, Map<String, String> data) {
		
		TestResult testResult = new TestResult();

		if (!passed) {
			if (message == null) {
				testResult.setMessage("No message provided for the failing testResult");
			}
			else if (message.isEmpty()) {
				testResult.setMessage("No content for the message provided for the failing testResult");
			}
		}
		
		if (duration < 0) {
			throw new IllegalArgumentException("The duration cannot be negative.");
		}

		if (key != null && !key.isEmpty()) {
			testResult.setKey(key);
		}

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
		
		if (tickets != null) {
			testResult.addTickets(tickets);
		}
		
		if (data != null) {
			testResult.addData(data);
		}
		
		return testResult;
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

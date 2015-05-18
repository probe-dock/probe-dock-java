package io.probedock.client.commons.optimize.v1;

import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.common.model.v1.TestReport;
import io.probedock.client.common.model.v1.TestResult;
import io.probedock.client.common.model.v1.TestRun;
import io.probedock.client.common.utils.FootprintGenerator;
import io.probedock.client.commons.optimize.Optimizer;
import io.probedock.client.commons.optimize.OptimizerStore;

import java.util.Map;

/**
 * Payload optimizer that remove some fields from the final
 * payload to optimize what is sent to Probe Dock server.
 * 
 * This optimizer is expected to work for a {@link TestRun} in
 * version 1 of the API level
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class TestRunOptimizer implements Optimizer {
	@Override
	public ProbeTestRun optimize(OptimizerStore store, ProbeTestRun optimizable) {
		if (store == null) {
			throw new IllegalArgumentException("The optimizer store cannot be null");
		}
		
		if (optimizable == null) {
			throw new IllegalArgumentException("The optimizable object cannot be null");
		}
		
		if (optimizable.getClass() != TestRun.class) {
			throw new IllegalArgumentException("The test run given is not the one that is expected.");
		}

		TestRun originalTestRun = (TestRun) optimizable;
		TestRun optimizedTestRun = copyTestRun(originalTestRun);

		for (TestResult originalResult : originalTestRun.getTestResults()) {
			// Calculate the footprint
			String footprint = footprint(originalResult);

			// Copy the testResult in an optimized way
			optimizedTestRun.getTestResults().add(
				copyTestResult(
					originalResult,
					store.testHasChanged(originalTestRun.getProjectId(), originalTestRun.getProjectVersion(), originalResult.getKey(), footprint)
				)
			);

			// Store the footprint
			store.storeTestFootprint(originalTestRun.getProjectId(), originalTestRun.getProjectVersion(), originalResult.getKey(), footprint);
		}
		
		return optimizedTestRun;
	}
	
	/**
	 * Copy a test run
	 * 
	 * @param testRun TestResult run to copy
	 * @return TestResult run copied
	 */
	private TestRun copyTestRun(TestRun testRun) {
		TestRun newTestRun = new TestRun();

		newTestRun.setProjectId(testRun.getProjectId());
		newTestRun.setProjectVersion(testRun.getProjectVersion());
		newTestRun.setDuration(testRun.getDuration());

		for (TestReport testReport : testRun.getTestReports()) {
			newTestRun.getTestReports().add(copyTestReport(testReport));
		}

		return newTestRun;
	}
	
	/**
	 * Copy a testResult
	 * 
	 * @param testResult TestResult to copy
	 * @param full Define if full attributes should be copied or not
	 * @return The testResult copied
	 */
	private TestResult copyTestResult(TestResult testResult, boolean full) {
		TestResult newTestResult = new TestResult();
		
		newTestResult.setDuration(testResult.getDuration());
		newTestResult.setKey(testResult.getKey());
		newTestResult.setPassed(testResult.isPassed());
		newTestResult.setMessage(testResult.getMessage());

		if (full) {
			newTestResult.setActive(testResult.isActive());
			newTestResult.setCategory(testResult.getCategory());
			newTestResult.setName(testResult.getName());
			newTestResult.addTags(testResult.getTags());
			newTestResult.addTickets(testResult.getTickets());
			newTestResult.addData(testResult.getData());
		}
		
		return newTestResult;
	}

	/**
	 * Copy a test report
	 *
	 * @param testReport The test report to copy
	 * @return The testReport copied
	 */
	private TestReport copyTestReport(TestReport testReport) {
		TestReport newTestReport = new TestReport();

		newTestReport.setUid(testReport.getUid());

		return newTestReport;
	}

	/**
	 * Compute the footprint for a testResult
	 * 
	 * @param testResult The testResult for which the footprint is computed
	 * @return The footprint computed
	 */
	private String footprint(TestResult testResult) {
		StringBuilder sb = new StringBuilder();
		
		sb
			.append(testResult.getCategory())
			.append(testResult.getName())
			.append(testResult.isActive());
		
		for (String tag : testResult.getTags()) {
			sb.append(tag);
		}
		
		for (String ticket : testResult.getTickets()) {
			sb.append(ticket);
		}
		
		for (Map.Entry<String, String> entry : testResult.getData().entrySet()) {
			sb.append(entry.getKey()).append(entry.getValue());
		}
	
		return FootprintGenerator.footprint(sb.toString());
	}
}
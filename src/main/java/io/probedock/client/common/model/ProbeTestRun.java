package io.probedock.client.common.model;

import io.probedock.client.commons.optimize.Optimizer;

import java.util.List;

/**
 * Probe Dock root payload to send test results to the server.
 *
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public interface ProbeTestRun {
	/**
	 * @return Retrieve the API version
	 */
	String getApiVersion();
	
	/**
	 * @return Get the optimizer corresponding to the right payload version
	 */
	Optimizer getOptimizer();

	/**
	 * @return The project version
	 */
	String getProjectVersion();

	/**
	 * @return The Probe Dock project identifier
	 */
	String getProjectId();

	/**
	 * @return The duration of the test run
	 */
	long getDuration();

	/**
	 * @return The list of test results
	 */
	List<? extends ProbeTestResult> getTestResults();

	/**
	 * @return The list of test reports
	 */
	List<? extends ProbeTestReport> getTestReports();
}

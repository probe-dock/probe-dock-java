package io.probedock.client.common.model;

import java.util.List;
import java.util.Map;

/**
 * Probe Dock root payload to send test results to the server.
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public interface ProbeTestRun {
	String PROBEDOCK_REPORT_UID = "probedock.report.uid";

	String PROBEDOCK_SCM_NAME = "scm.name";
	String PROBEDOCK_SCM_VERSION = "scm.version";
	String PROBEDOCK_SCM_DIRTY = "scm.dirty";
	String PROBEDOCK_SCM_BRANCH = "scm.branch";
	String PROBEDOCK_SCM_COMMIT = "scm.commit";
	String PROBEDOCK_SCM_REMOTE_NAME = "scm.remote.name";
	String PROBEDOCK_SCM_REMOTE_FETCH_URL = "scm.remote.url.fetch";
	String PROBEDOCK_SCM_REMOTE_PUSH_URL = "scm.remote.url.push";
	String PROBEDOCK_SCM_REMOTE_AHEAD = "scm.remote.ahead";
	String PROBEDOCK_SCM_REMOTE_BEHIND = "scm.remote.behind";

	/**
	 * @return Retrieve the API version
	 */
	String getApiVersion();

	/**
	 * @return The project version
	 */
	String getVersion();

	/**
	 * @return The Probe Dock project identifier
	 */
	String getProjectId();

	/**
	 * @return The duration of the test run
	 */
	long getDuration();

	/**
	 * @return The pipeline
	 */
	String getPipeline();

	/**
	 * @return The stage in the pipeline
	 */
	String getStage();

	/**
	 * @return The execution tests context
	 */
	ProbeContext getContext();

	/**
	 * @return The probe info
	 */
	ProbeInfo getProbe();

	/**
	 * @return The custom data of the test run
	 */
	Map<String, String> getData();

	/**
	 * @return The list of test results
	 */
	List<? extends ProbeTestResult> getTestResults();

	/**
	 * @return The list of test reports
	 */
	List<? extends ProbeTestReport> getTestReports();
}

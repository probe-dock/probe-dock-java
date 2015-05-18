package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.commons.optimize.Optimizer;
import io.probedock.client.commons.optimize.v1.TestRunOptimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of test testResults for a specific projectVersion of a project.
 *
 * @author Simon Oulevay <simon.oulevay@probe-dock.io>
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class TestRun implements ProbeTestRun {
	private static final String API_VERSION = "v1";

	@JsonProperty("projectId")
	private String projectId;

	@JsonProperty("version")
	private String projectVersion;

	@JsonProperty("duration")
	private long duration;

	@JsonProperty("results")
	private List<TestResult> testResults = new ArrayList<>();

	@JsonProperty("reports")
	private List<TestReport> testReports = new ArrayList<>();

	@Override
	public String getApiVersion() {
		return API_VERSION;
	}

	@Override
	@JsonIgnore
	public Optimizer getOptimizer() {
		return new TestRunOptimizer();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public List<TestResult> getTestResults() {
		return testResults;
	}

	@Override
	public List<TestReport> getTestReports() {
		return testReports;
	}

	@Override
	public String toString() {
		final StringBuilder sbTestResults = new StringBuilder();
		final StringBuilder sbTestReports = new StringBuilder();

		for (TestResult testResult : testResults) {
			sbTestResults.append(testResult).append(", ");
		}

		for (TestReport testReport : testReports) {
			sbTestResults.append(testReport).append(", ");
		}

		return
			"TestRun: [" +
				"ProjectId: " + projectId + ", " +
				"Version: " + projectVersion + ", " +
				"Duration: " + duration + ", " +
				"Results: [" + sbTestResults.toString().replaceAll(", $", "") + "]" +
				"Reports: [" + sbTestReports.toString().replaceAll(", $", "") + "]" +
			"]";
	}
}

package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.probedock.client.common.model.ProbeTestRun;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A list of test testResults for a specific version of a project.
 *
 * @author Simon Oulevay simon.oulevay@probedock.io
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class TestRun implements ProbeTestRun {
	@JsonIgnore
	private static final String API_VERSION = "v1";

	@JsonProperty("projectId")
	private String projectId;

	@JsonProperty("version")
	private String version;

	@JsonProperty("duration")
	private long duration;

	@JsonProperty("pipeline")
	private String pipeline;

	@JsonProperty("stage")
	private String stage;

	@JsonUnwrapped
	private Context context;

	@JsonProperty("probe")
	private Probe probe;

	@JsonProperty("results")
	private List<TestResult> testResults = new ArrayList<>();

	@JsonProperty("data")
	private Map<String, String> data;

	@JsonProperty("reports")
	private List<TestReport> testReports = new ArrayList<>();

	@Override
	@JsonIgnore
	public String getApiVersion() {
		return API_VERSION;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String getPipeline() {
		return pipeline;
	}

	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Probe getProbe() {
		return probe;
	}

	public void setProbe(Probe probe) {
		this.probe = probe;
	}

	@Override
	public Map<String, String> getData() {
		return data;
	}

	public void addData(Map<String, String> data) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}
		this.data.putAll(data);
	}

	public void addData(String key, String value) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}
		this.data.put(key, value);
	}

	/**
	 * Add a data to the metadata of the test run. If the value is null, no entry is added
	 *
	 * @param key The key of the value
	 * @param value The value
     */
	public void addDataNullAvoided(String key, String value) {
		if (value != null) {
			addData(key, value);
		}
	}

	/**
	 * Add a data to the metadata of the test run. If the value is null, no entry is added. Use toString().
	 * @param key
	 * @param value
     */
	public void addDataNullAvoided(String key, Object value) {
		if (value != null) {
			addData(key, value.toString());
		}
	}

	@Override
	public List<TestResult> getTestResults() {
		return testResults;
	}

	public void addTestResults(List<TestResult> results) {
		testResults.addAll(results);
	}

	@Override
	public List<TestReport> getTestReports() {
		return testReports;
	}

	@Override
	public String toString() {
		final StringBuilder sbTestResults = new StringBuilder();
		for (TestResult testResult : testResults) {
			sbTestResults.append(testResult).append(", ");
		}

		final StringBuilder sbTestReports = new StringBuilder();
		for (TestReport testReport : testReports) {
			sbTestResults.append(testReport).append(", ");
		}

		final StringBuilder sbData = new StringBuilder();
		if (data != null) {
			for (Map.Entry<String, String> e : data.entrySet()) {
				sbData.append("Data[Key: [").append(e.getKey()).append("], Value: [").append(e.getValue()).append("], ");
			}
		}

		return
			"TestRun: [" +
				"ProjectId: " + projectId + ", " +
				"Version: " + version + ", " +
				"Duration: " + duration + ", " +
				"Pipeline: " + pipeline + ", " +
				"Stage: " + stage + ", " +
				"Context: " + context + ", " +
				"Probe: " + probe + ", " +
				"Data: [" + sbData.toString().replaceAll(", $", "") + "]" +
				"Results: [" + sbTestResults.toString().replaceAll(", $", "") + "]" +
				"Reports: [" + sbTestReports.toString().replaceAll(", $", "") + "]" +
			"]";
	}
}

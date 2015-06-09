package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.probedock.client.common.model.ProbeTestResult;

import java.util.*;
import java.util.Map.Entry;

/**
 * TestResult class to store the data related to tests
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class TestResult implements ProbeTestResult {

	@JsonProperty("k")
	private String key;

	@JsonProperty("f")
	private String fingerprint;

	@JsonProperty("n")
	private String name;

	@JsonProperty("p")
	private boolean passed;

	@JsonProperty("v")
	private Boolean active;

	@JsonProperty("d")
	private long duration;

	@JsonProperty("m")
	private String message;
	
	@JsonProperty("c")
	private String category;

	@JsonProperty("o")
	private Set<String> contributors;

	@JsonProperty("g")
	private Set<String> tags;
	
	@JsonProperty("t")
	private Set<String> tickets;
	
	@JsonProperty("a")
	private Map<String, String> data;
	
	public TestResult() {}
	
	public TestResult(String key, String fingerprint, String name, Long duration, boolean passed, String message, String category) {
		this.key = key;
		this.fingerprint = fingerprint;
		this.name = name;
		this.duration = duration;
		this.passed = passed;
		this.message = message;
		this.category = category;
	}

	public TestResult(String key, String fingerprint, String name, Long duration, boolean passed, String message, String category, Boolean active) {
		this.key = key;
		this.fingerprint = fingerprint;
		this.name = name;
		this.duration = duration;
		this.passed = passed;
		this.message = message;
		this.category = category;
		this.active = active;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public Boolean isActive() {
		return active;
	}

	public final void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public Set<String> getContributors() {
		return contributors;
	}

	public void addContributors(String[] contributors) {
		addContributors(Arrays.asList(contributors));
	}

	public void addContributors(List<String> contributors) {
		addContributors(new HashSet<>(contributors));
	}

	public void addContributors(Set<String> contributors) {
		if (this.contributors == null) {
			this.contributors = new HashSet<>();
		}
		this.contributors.addAll(contributors);
	}

	public void addContributor(String contributor) {
		if (contributors == null) {
			contributors = new HashSet<>();
		}
		contributors.add(contributor);
	}

	@Override
	public Set<String> getTags() {
		return tags;
	}
	
	public void addTags(String[] tags) {
		addTags(Arrays.asList(tags));
	}
	
	public void addTags(List<String> tags) {
		addTags(new HashSet<>(tags));
	}
	
	public void addTags(Set<String> tags) {
		if (this.tags == null) {
			this.tags = new HashSet<>();
		}
		this.tags.addAll(tags);
	}
	
	public void addTag(String tag) {
		if (tags == null) {
			tags = new HashSet<>();
		}
		tags.add(tag);
	}

	@Override
	public Set<String> getTickets() {
		return tickets;
	}

	public void addTickets(String[] tickets) {
		addTickets(Arrays.asList(tickets));
	}
	
	public void addTickets(List<String> tickets) {
		addTickets(new HashSet<>(tickets));
	}
	
	public void addTickets(Set<String> tickets) {
		if (this.tickets == null) {
			this.tickets = new HashSet<>();
		}
		this.tickets.addAll(tickets);
	}
	
	public void addTicket(String ticket) {
		if (tickets == null) {
			tickets = new HashSet<>();
		}
		tickets.add(ticket);
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
	
	@Override
	public String toString() {
		StringBuilder sbContributors = new StringBuilder();
		if (contributors != null) {
			for (String c : contributors) {
				sbContributors.append("Contributor: [").append(c).append(", ");
			}
		}

		StringBuilder sbTags = new StringBuilder();
		if (tags != null) {
			for (String g : tags) {
				sbTags.append("Tag: [").append(g).append(", ");
			}
		}
		
		StringBuilder sbTickets = new StringBuilder();
		if (tickets != null) {
			for (String t : tickets) {
				sbTickets.append("Ticket: [").append(t).append(", ");
			}
		}

		StringBuilder sbData = new StringBuilder();
		if (data != null) {
			for (Entry<String, String> e : data.entrySet()) {
				sbData.append("Data[Key: [").append(e.getKey()).append("], Value: [").append(e.getValue()).append("], ");
			}
		}
		
		return 
			"TestResult: [" +
				"Key: " + key + ", " +
				"Fingerprint: " + fingerprint + ", " +
				"Name: " + name + ", " +
				"Passed: " + passed + ", " +
				"Active: " + active + ", " +
				"Duration: " + duration + ", " +
				"Message: " + message  + ", " +
				"Category: " + category + ", " +
				"Active: " + active + ", " +
				"Contributors: [" + sbContributors.toString().replaceAll(", $", "], ") +
				"Tags: [" + sbTags.toString().replaceAll(", $", "]") + "], " +
				"Tickets: [" + sbTickets.toString().replaceAll(", $", "]") + "], " +
				"Data: [" + sbData.toString().replaceAll(", $", "") + "]" + 
			"]";
	}
}

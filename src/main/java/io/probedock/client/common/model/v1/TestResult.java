package io.probedock.client.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.probedock.client.common.model.ProbeTestResult;

import java.util.*;
import java.util.Map.Entry;

/**
 * TestResult class to store the data related to tests
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class TestResult implements ProbeTestResult {

	@JsonProperty("k")
	private String key;
	
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
	
	@JsonProperty("g") // cached
	private Set<String> tags;
	
	@JsonProperty("t") // cached
	private Set<String> tickets;
	
	@JsonProperty("a") // cached
	private Map<String, String> data;
	
	public TestResult() {}
	
	public TestResult(String key, String name, Long duration, boolean passed, String message, String category) {
		this.key = key;
		this.name = name;
		this.duration = duration;
		this.passed = passed;
		this.message = message;
		this.category = category;
	}

	public TestResult(String key, String name, Long duration, boolean passed, String message, String category, Boolean active) {
		this.key = key;
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
	public boolean isActive() {
		return active;
	}

	public final void setActive(Boolean active) {
		this.active = active;
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
				"Name: " + name + ", " +
				"Passed: " + passed + ", " +
				"Active: " + active + ", " +
				"Duration: " + duration + ", " +
				"Message: " + message  + ", " +
				"Category: " + category + ", " +
				"Status: " + active + ", " +
				"Tags: [" + sbTags.toString().replaceAll(", $", "]") + "], " +
				"Tickets: [" + sbTickets.toString().replaceAll(", $", "]") + "], " +
				"Data: [" + sbData.toString().replaceAll(", $", "") + "]" + 
			"]";
	}
}

package io.probedock.client.core.filters;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure to apply the filtering
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class FilterTargetData {
	private final String tags;
	private final String tickets;
	private final String name;
	private final String technicalName;
	private final String key;

	public String getKey() {
		return key;
	}

	/**
	 * Constructor
	 * 
	 * @param m Method
	 * @param mAnnotation Method annotation
	 * @param cAnnotation Class annotation
	 */
	public FilterTargetData(Method m, ProbeTest mAnnotation, ProbeTestClass cAnnotation) {
		this.tags = mergeTags(mAnnotation, cAnnotation);
		this.tickets = mergeTickets(mAnnotation, cAnnotation);
		this.name = testName(m, mAnnotation);
		this.technicalName = m.getDeclaringClass() + "." + m.getName();
		this.key = mAnnotation != null ? mAnnotation.key() : "";
	}

	/**
	 * Constructor
	 * 
	 * @param tags Tags
	 * @param tickets Tickets
	 * @param technicalName Technical name
	 * @param name Name
	 * @param key Key
	 */
	public FilterTargetData(String tags, String tickets, String technicalName, String name, String key) {
		this.tags = tags;
		this.tickets = tickets;
		this.name = name;
		this.technicalName = technicalName;
		this.key = key;
	}
	
	/**
	 * Match any condition
	 * 
	 * @param lookupAny Filter text
	 * @return True if match, false otherwise
	 */
	public boolean anyMatch(String lookupAny) {
		return
			keyMatch(lookupAny) || nameMatch(lookupAny) || 
			tagMatch(lookupAny) || ticketMatch(lookupAny);
	}

	/**
	 * Match key condition
	 * 
	 * @param lookupKey Filter text
	 * @return True if match, false otherwise
	 */
	public boolean keyMatch(String lookupKey) {
		if (key.isEmpty()) {
			return technicalName.contains(lookupKey);
		}
		else {
			return key.contains(lookupKey);
		}
	}

	/**
	 * Match name condition
	 * 
	 * @param lookupName Filter text
	 * @return True if match, false otherwise
	 */
	boolean nameMatch(String lookupName) {
		return name.contains(lookupName);
	}

	/**
	 * Match tag condition
	 * 
	 * @param lookupAny Filter text
	 * @return True if match, false otherwise
	 */
	boolean tagMatch(String lookupTag) {
		return tags.contains(lookupTag);
	}

	/**
	 * Match ticket condition
	 * 
	 * @param lookupAny Filter text
	 * @return True if match, false otherwise
	 */
	boolean ticketMatch(String lookupTicket) {
		return tickets.contains(lookupTicket);
	}

	/**
	 * Retrieve the name of a test
	 * 
	 * @param method The method name
	 * @param mAnnotation Method annotation
	 * @return The test name
	 */
	private static String testName(Method method, ProbeTest mAnnotation) {
		if (mAnnotation.name() != null && !mAnnotation.name().isEmpty()) {
			return mAnnotation.name();
		}
		
		return method.getName();
	}
	
	/**
	 * Create a string containing all the tags from method and class annotations
	 * 
	 * @param mAnnotation Method annotation
	 * @param cAnnotation Class annotation
	 * @return The string of tags
	 */
	private static String mergeTags(ProbeTest mAnnotation, ProbeTestClass cAnnotation) {
		List<String> tags = new ArrayList<>();
		
		if (mAnnotation != null) {
			tags.addAll(Arrays.asList(mAnnotation.tags()));
		}
		
		if (cAnnotation != null) {
			tags.addAll(Arrays.asList(cAnnotation.tags()));
		}

		return Arrays.toString(tags.toArray(new String[tags.size()]));
	}

	/**
	 * Create a string containing all the tickets from method and class annotations
	 * 
	 * @param mAnnotation Method annotation
	 * @param cAnnotation Class annotation
	 * @return The string of tickets
	 */
	private static String mergeTickets(ProbeTest mAnnotation, ProbeTestClass cAnnotation) {
		List<String> tickets = new ArrayList<>();
		
		if (mAnnotation != null) {
			tickets.addAll(Arrays.asList(mAnnotation.tickets()));
		}
		
		if (cAnnotation != null) {
			tickets.addAll(Arrays.asList(cAnnotation.tickets()));
		}
		
		return Arrays.toString(tickets.toArray(new String[tickets.size()]));
	}
	
}
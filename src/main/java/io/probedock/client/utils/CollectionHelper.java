package io.probedock.client.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Method utilities to manage collection such the tags
 * and/or the tickets
 * 
 * @author Laurent Prévost, laurent.prevost@lotaris.com
 */
public class CollectionHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionHelper.class);
	
	private static final Pattern tagPattern = Pattern.compile("[a-zA-z0-9-_]*");
	
	/**
	 * Retrieve the tags and compile them from the different sources
	 * @param baseTags a list of based tags
	 * @param methodAnnotation The method annotation that could contain tags
	 * @param classAnnotation The class annotation that could contain tags
	 * @return The tags from the different sources
	 */
	public static Set<String> getTags(Set<String> baseTags, ProbeTest methodAnnotation, ProbeTestClass classAnnotation) {
		Set<String> tags;
		if (baseTags == null) {
			tags = new HashSet<>();
		}
		else {
			tags = populateTags(baseTags, new HashSet<String>());
		}
		
		if (classAnnotation != null && classAnnotation.tags() != null) {
			tags = populateTags(new HashSet<>(Arrays.asList(classAnnotation.tags())), tags);
		}
		
		if (methodAnnotation != null && methodAnnotation.tags() != null) {
			tags = populateTags(new HashSet<>(Arrays.asList(methodAnnotation.tags())), tags);
		}
		
		return tags;		
	}

	/**
	 * Populate the source with the destination tags only if they
	 * match the pattern rules for the tags
	 * @param source The source to check
	 * @param destination The destination to fill
	 * @return The destination updated
	 */
	private static Set<String> populateTags(Set<String> source, Set<String> destination) {
		for (String tag : source) {
			if (!tagPattern.matcher(tag).matches()) {
				LOGGER.warn("The tag {} does not respect the following pattern {} and is ignored", tag, tagPattern.pattern());
			}
			else if (destination.contains(tag)) {
				LOGGER.info("The tag {} is already present in the collection and is ingored", tag);
			}
			else {
				destination.add(tag);
			}
		}
		return destination;
	}
	
	/**
	 * Retrieve the tickets and compile them from the different sources
	 * @param basedTickets A list of based tickets
	 * @param methodAnnotation The method annotation that could contain tickets
	 * @param classAnnotation The class annotation that could contain tickets
	 * @return The tickets from the different sources
	 */
	public static Set<String> getTickets(Set<String> basedTickets, ProbeTest methodAnnotation, ProbeTestClass classAnnotation) {
		Set<String> tickets;
		if (basedTickets == null) {
			tickets = new HashSet<>();
		}
		else {
			tickets = populateTickets(basedTickets, new HashSet<String>());
		}
		
		if (classAnnotation != null && classAnnotation.tickets() != null) {
			tickets = populateTickets(new HashSet<>(Arrays.asList(classAnnotation.tickets())), tickets);
		}
		
		if (methodAnnotation != null && methodAnnotation.tickets() != null) {
			tickets = populateTickets(new HashSet<>(Arrays.asList(methodAnnotation.tickets())), tickets);
		}
		
		return tickets;		
	}

	/**
	 * Populate the source with the destination tags only if they
	 * match the pattern rules for the tags
	 * @param source The source to check
	 * @param destination The destination to fill
	 * @return The destination updated
	 */
	private static Set<String> populateTickets(Set<String> source, Set<String> destination) {
		for (String ticket : source) {
			if (destination.contains(ticket)) {
				LOGGER.info("The ticket {} is already present in the collection and is ingored", ticket);
			}
			else {
				destination.add(ticket);
			}
		}
		return destination;
	}
}

package io.probedock.client.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Method utilities to manage collection such the tags
 * and/or the tickets
 * 
 * @author Laurent Prévost, laurent.prevost@lotaris.com
 */
public class CollectionHelper {
	private static final Logger LOGGER = Logger.getLogger(CollectionHelper.class.getCanonicalName());
	
	private static final Pattern tagPattern = Pattern.compile("[a-zA-z0-9-_]*");

	private static final Pattern emailPattern = Pattern.compile(
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
	);

	/**
	 * Retrieve the contributors and compile them from the different sources
	 *
	 * @param baseContributors a list of base contributors
	 * @param methodAnnotation The method annotation that could contain contributors
	 * @param classAnnotation The class annotation that could contain contributors
	 * @return The contributors from the different sources
	 */
	public static Set<String> getContributors(Set<String> baseContributors, ProbeTest methodAnnotation, ProbeTestClass classAnnotation) {
		Set<String> contributors;
		if (baseContributors == null) {
			contributors = new HashSet<>();
		}
		else {
			contributors = populateContributors(baseContributors, new HashSet<String>());
		}

		if (classAnnotation != null && classAnnotation.contributors() != null) {
			contributors = populateContributors(new HashSet<>(Arrays.asList(classAnnotation.contributors())), contributors);
		}

		if (methodAnnotation != null && methodAnnotation.contributors() != null) {
			contributors = populateContributors(new HashSet<>(Arrays.asList(methodAnnotation.contributors())), contributors);
		}

		return contributors;
	}

	/**
	 * Populate the source with the destination contributors only if they match the pattern rules for the contributors
	 *
	 * @param source The source to check
	 * @param destination The destination to fill
	 * @return The destination updated
	 */
	private static Set<String> populateContributors(Set<String> source, Set<String> destination) {
		for (String contributor : source) {
			if (!emailPattern.matcher(contributor).matches()) {
				LOGGER.warning("The contributor '" + contributor + "' does not respect the email pattern " + emailPattern.pattern() + " and is ignored");
			}
			else if (destination.contains(contributor)) {
				LOGGER.info("The contributor '" + contributor + "' is already present in the collection and is ignored");
			}
			else {
				destination.add(contributor);
			}
		}
		return destination;
	}


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
				LOGGER.warning("The tag " + tag + " does not respect the following pattern " + tagPattern.pattern() + " and is ignored");
			}
			else if (destination.contains(tag)) {
				LOGGER.info("The tag " + tag + " is already present in the collection and is ingored");
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
				LOGGER.info("The ticket " + ticket + " is already present in the collection and is ingored");
			}
			else {
				destination.add(ticket);
			}
		}
		return destination;
	}
}

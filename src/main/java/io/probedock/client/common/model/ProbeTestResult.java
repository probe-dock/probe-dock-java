package io.probedock.client.common.model;

import java.util.Map;
import java.util.Set;

/**
 * Define what a test result should be
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface ProbeTestResult {
	/**
	 * @return The unique identifier of the test.
	 */
	String getKey();

	/**
	 * @return The fingerprint of the test (hash of package.class.method)
	 */
	String getFingerprint();

	/**
	 * @return The name of the test
	 */
	String getName();

	/**
	 * @return The duration of the test execution
	 */
	long getDuration();
	
	/**
	 * @return If the test is passed or not
	 */
	boolean isPassed();

	/**
	 * @return If the test is active
	 */
	Boolean isActive();

	/**
	 * @return The message of the test result
	 */
	String getMessage();

	/**
	 * @return The category of the test
	 */
	String getCategory();

	/**
	 * @return Set of tags
	 */
	Set<String> getTags();

	/**
	 * @return Set of contributors
	 */
	Set<String> getContributors();

	/**
	 * @return Set of tickets
	 */
	Set<String> getTickets();

	/**
	 * @return Map of data
	 */
	Map<String, String> getData();
}
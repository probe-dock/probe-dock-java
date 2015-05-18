package io.probedock.client.commons.optimize;

import io.probedock.client.common.config.Configuration;

/**
 * Allow to create optimizer stores to know if a test has changed or not
 * between two optimizations
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public interface OptimizerStore {
	/**
	 * Start the optimizer store
	 * 
	 * @param configuration The configuration to use
	 */
	void start(Configuration configuration);
	
	/**
	 * Stop the optimizer store
	 * @param persist Persist the cache or not
	 */
	void stop(boolean persist);
	
	/**
	 * Check if a test has changed or not
	 * 
	 * @param project The project name
	 * @param version The project version
	 * @param key The key of the test
	 * @param footprint The footprint to identify tests that change between two optimization
	 * @return True if the test has changed since the last optimization, false otherwise
	 */
	boolean testHasChanged(String project, String version, String key, String footprint);

	/**
	 * Store a footprint for a test
	 * 
	 * @param project The project name
	 * @param version The project version
	 * @param key The key of the test
	 * @param footprint The footprint to store for the test
	 */
	void storeTestFootprint(String project, String version, String key, String footprint);
}

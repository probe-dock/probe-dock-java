package io.probedock.client.core.cache;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.commons.optimize.OptimizerStore;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A default implementation of an optimizer store. This optimizer store
 * will cache the data in file structure in the Probe Dock directory
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class CacheOptimizerStore implements OptimizerStore {
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheOptimizerStore.class);

	private Configuration configuration;
	
	private Map<CacheKey, Map<String, String>> caches = new HashMap<>();

	@Override
	public void start(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void stop(boolean persist) {
		if (persist) {
			persistCaches();
		}
	}
	
	@Override
	public boolean testHasChanged(String projectName, String projectVersion, String key, String footprint) {
		Map<String, String> cache = getCache(projectName, projectVersion);

		// If hashed calculated
		if (footprint != null) {
			String cachedFootprint = cache.get(key);

			// If not changed
			if (cachedFootprint != null && footprint.equals(cachedFootprint)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void storeTestFootprint(String project, String version, String key, String footprint) {
		getCache(project, version).put(key, footprint);
	}
	
	/**
	 * Clean all the caches
	 */
	public void cleanCaches() {
		try {
			FileUtils.cleanDirectory(getCacheDir());
		}
		catch (IOException ioe) {
			LOGGER.warn("Unable to clean the cache directory.", ioe);
		}
	}
	
	/**
	 * Retrieve the cache for a project in a version
	 * 
	 * @param projectName The project name
	 * @param projectVersion The project version
	 * @return The cache found. If none available, a new one is created
	 */
	private Map<String, String> getCache(String projectName, String projectVersion) {
		if (!caches.containsKey(getCacheKey(projectName, projectVersion))) {
			caches.put(getCacheKey(projectName, projectVersion), loadOrCreateCache(projectName, projectVersion));
		}

		return caches.get(getCacheKey(projectName, projectVersion));
	}
	
	/**
	 * Try to load the cache from file, if not available, a new cache is created
	 * 
	 * @param projectName The project name
	 * @param projectVersion The project version
	 * @return The cache loaded or created
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> loadOrCreateCache(String projectName, String projectVersion) {
		if (projectName != null && projectVersion != null) {
			// Get the project dir or create it
			File projectDir = new File(getCacheDir(), projectName);
			if (!projectDir.exists()) {
				projectDir.mkdir();
			}
			
			// Get the version file or create it
			File versionFile = new File(projectDir, projectVersion);
			if (versionFile.exists()) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(versionFile))) {
					return (Map<String, String>) ois.readObject();
				}
				catch (IOException ioe) {
					LOGGER.warn("Unable to read the cache for {}-{}", projectName, projectVersion);
				}
				catch (ClassNotFoundException cnfe) {
					LOGGER.warn("The cache for {}-{} is corrupted.", projectName, projectVersion);
				}
			}
		}
		
		return new HashMap<>();
	}	
	
	/**
	 * Create a cache key that is an entry in the cache map
	 * 
	 * @param projectName Project name
	 * @param projectVersion Project version
	 * @return The cache key created
	 */
	private CacheKey getCacheKey(String projectName, String projectVersion) {
		return new CacheKey(projectName, projectVersion);
	}	
	
	/**
	 * Get the cache directory. if not exist, it is created and returned.
	 * 
	 * @return The cache base directory
	 */
	private File getCacheDir() {
		// Get the root cache dir
		File cacheDir = new File(configuration.getOptimizerCacheDir());
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}

		// Get the directory that represent the host directory
		File probeDockCacheDir = new File(cacheDir, configuration.getServerConfiguration().getBaseUrlFootprint());
		if (!probeDockCacheDir.exists()) {
			probeDockCacheDir.mkdirs();
		}
		
		return probeDockCacheDir;
	}	
	
	/**
	 * Persist the cache that have been updated
	 */
	private void persistCaches() {
		for (Entry<CacheKey, Map<String, String>> cacheEntry : caches.entrySet()) {
			persistCache(cacheEntry.getKey().projectName, cacheEntry.getKey().projectVersion, cacheEntry.getValue());
		}
	}
	
	/**
	 * Persist a specific cache
	 * 
	 * @param projectId Project id
	 * @param projectVersion Project version
	 * @param cache The cache to persist
	 */
	private void persistCache(String projectId, String projectVersion, Map<String, String> cache) {
		File cacheFile = new File(getCacheDir(), projectId + "/" + projectVersion);

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))){
			oos.writeObject(cache);
		}
		catch (IOException ioe) {
			LOGGER.warn("Unable to create the cache file {}/{}/{}", getCacheDir().getAbsoluteFile(), projectId, projectVersion);
		}
	}
	
	/**
	 * Class to represent a cache key in the map
	 */
	private class CacheKey implements Serializable {
		private String projectName;
		private String projectVersion;
		
		/**
		 * Constructor
		 * 
		 * @param projectName Project name
		 * @param projectVersion Project version
		 */
		private CacheKey(String projectName, String projectVersion) {
			this.projectName = projectName;
			this.projectVersion = projectVersion;
		}

		@Override
		public boolean equals(Object obj) {
			return 
				projectName.equals(((CacheKey) obj).projectName) &&
				projectVersion.equals(((CacheKey) obj).projectVersion);
		}

		@Override
		public int hashCode() {
			return projectName.hashCode() * 3 + projectVersion.hashCode() * 5;
		}
	}
}

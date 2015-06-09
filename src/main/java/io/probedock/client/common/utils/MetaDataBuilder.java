package io.probedock.client.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The meta data builder is a helper class to facilitate the creation and
 * addition of new meta data to the different Probe Dock data structure.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class MetaDataBuilder {
	/**
	 * Stores the data values
	 */
	private Map<String, String> data = new HashMap<>();

	/**
	 * Add a new meta data in the data store
	 *
	 * @param name The name of the meta data
	 * @param value Its value
	 * @return This
	 */
	public MetaDataBuilder add(String name, String value) {
		data.put(name, value);
		return this;
	}

	/**
	 * Merge the meta data builder given in the current one
	 *
	 * @param metaDataBuilder The meta data build to merge
	 * @return This
	 */
	public MetaDataBuilder add(MetaDataBuilder metaDataBuilder) {
		data.putAll(metaDataBuilder.data);
		return this;
	}

	/**
	 * @return The meta data ready to set to the test run or test result.
	 */
	public Map<String, String> toMetaData() {
		return data;
	}
}

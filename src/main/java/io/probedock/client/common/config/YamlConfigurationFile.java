package io.probedock.client.common.config;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YAML configuration file to handle YAML format for the Apache Configuration framework.
 *
 * @author Simon Oulevay simon.oulevay@probedock.io
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class YamlConfigurationFile extends AbstractFileConfiguration {

	private Yaml yaml;
	private String rootNodeName;
	private String serversProperty;
	private String categoriesByPackage;
	private ServerListConfiguration serverList;

	public YamlConfigurationFile(String filename, String rootNodeName, ServerListConfiguration serverList) throws ConfigurationException {
		this.yaml = new Yaml();
		this.rootNodeName = rootNodeName;
		this.serversProperty = rootNodeName + ".servers";
		this.categoriesByPackage = rootNodeName + ".java.categoriesByPackage";
		this.serverList = serverList;
		setFileName(filename);
		load();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void load(Reader in) throws ConfigurationException {

		final boolean previousAutoSave = isAutoSave();
		setAutoSave(false);

		final Object document = yaml.load(in);
		if (!(document instanceof Map)) {
			throw new ConfigurationException("Probe Dock configuration must be a map.");
		}

		loadYamlValue(document, rootNodeName);

		setAutoSave(previousAutoSave);
	}

	@SuppressWarnings("unchecked")
	private void loadYamlValue(Object value, String path) throws ConfigurationException {
		if (value instanceof Map) {

			if (path.equals(serversProperty)) {
				loadServers((Map<String, Object>) value);
				return;
			}

			// Provide the way to convert the YAML structure to the Apache config structure. We want to keep
			// the categories by package as a map and not to convert each node to another node in the Apache config.
			if (path.equals(categoriesByPackage)) {
				addProperty(path, value);
			}

			final Map map = (Map) value;
			for (final Object key : map.keySet()) {
				loadYamlValue(map.get(key), path + "." + key.toString());
			}
		} else if (value instanceof List) {

			final List list = (List) value;
			final List<String> stringList = new ArrayList<>(list.size());

			for (final Object element : list) {
				if (element != null) {
					stringList.add(element.toString());
				}
			}

			addPropertyDirect(path, stringList);
		} else {

			addProperty(path, value);
		}
	}

	@Override
	public void save(Writer out) throws ConfigurationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@SuppressWarnings("unchecked")
	private void loadServers(Map<String, Object> data) throws ConfigurationException {
		for (String name : data.keySet()) {
			if (!(data.get(name) instanceof Map)) {
				throw new ConfigurationException("Server \"" + name + "\" (at "
						+ serversProperty + "." + name + ") must be a map");
			}

			serverList.configureServer(name, (Map<String, Object>) data.get(name));
		}
	}
}

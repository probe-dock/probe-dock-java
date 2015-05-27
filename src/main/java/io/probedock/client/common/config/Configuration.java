package io.probedock.client.common.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Configuration for ProbeDock clients. Define general configuration
 * that should be present for each run of a client.
 * 
 * The configuration is not thread safe but it is not so critical.
 * Performance issues should be there.
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class Configuration {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

	private static final Pattern BOOLEAN_PATTERN = Pattern.compile("\\A(1|y|yes|t|true)\\Z", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Default home directory
	 */
	private static final String DEFAULT_HOMEDIR = System.getProperty("user.home") + "/.probedock";
	private static final String DEFAULT_CACHEDIR = DEFAULT_HOMEDIR + "/cache";
	
	/**
	 * Base configuration that should be present in the home directory
	 */
	private static final String BASE_CONFIG_PATH = ".probedock/config.yml";
	
	/**
	 * Configuration placed in the classpath
	 */
	private static final String CLASSPATH_CONFIG = "probedock.yml";
	
	/**
	 * The UID file name
	 */
	private static final String UID_FILE_NAME = "uid";
	
	/**
	 * Root node name of the tree configuration
	 */
	protected static final String P_ROOT_NODE_NAME = "probedock";

	private static final String ENV_PREFIX = "PROBE_DOCK_";

	/**
	 * Parameter names
	 */
	private static final String P_WORKSPACE 	= P_ROOT_NODE_NAME + ".workspace";
	private static final String P_SERVER 		= P_ROOT_NODE_NAME + ".server";
	private static final String P_TAGS 			= P_ROOT_NODE_NAME + ".tags";
	private static final String P_TICKETS 		= P_ROOT_NODE_NAME + ".tickets";
	private static final String P_CATEGORY 		= P_ROOT_NODE_NAME + ".category";
	private static final String P_PUBLISH 		= P_ROOT_NODE_NAME + ".publish";
	private static final String P_GENERATORSEED = P_ROOT_NODE_NAME + ".seed";

	private static final String P_PAYLOAD_PRINT	= P_ROOT_NODE_NAME + ".payload.print";
	private static final String P_PAYLAOD_CACHE	= P_ROOT_NODE_NAME + ".payload.cache";
	private static final String P_PAYLOAD_SAVE	= P_ROOT_NODE_NAME + ".payload.save";

	private static final String P_OPTIMIZER_CLASS 		= P_ROOT_NODE_NAME + ".java.optimizer.storeClass";
	private static final String P_OPTIMIZER_CACHE_DIR	= P_ROOT_NODE_NAME + ".java.optimizer.cacheDir";
	private static final String P_SERIALIZER_CLASS		= P_ROOT_NODE_NAME + ".java.serializerClass";

	private static final String P_PROJECT_API_ID				= P_ROOT_NODE_NAME + ".project.apiId";
	private static final String P_PROJECT_VERSION				= P_ROOT_NODE_NAME + ".project.version";
	private static final String P_PROJECT_CATEGORY				= P_ROOT_NODE_NAME + ".project.category";
	private static final String P_PROJECT_TAGS					= P_ROOT_NODE_NAME + ".project.tags";
	private static final String P_PROJECT_TICKETS				= P_ROOT_NODE_NAME + ".project.tickets";
	private static final String P_PROJECT_GENERATORSEED			= P_ROOT_NODE_NAME + ".project.seed";
	
	/**
	 * Not thread safe, not critical
	 */
	private static Configuration instance;
	
	/**
	 * Configuration
	 */
	protected CompositeConfiguration config;
	
	/**
	 * Server list configuration.
	 */
	private ServerListConfiguration serverList;
	
	/**
	 * Local cache of tags and tickets
	 */
	private Set<String> tags;
	private Set<String> tickets;
	
	private boolean disabled = false;
	
	/**
	 * Constructor
	 */
	protected Configuration() {
		config = new CompositeConfiguration();
		serverList = new ServerListConfiguration();
		
		try {
			config.addConfiguration(new YamlConfigurationFile(CLASSPATH_CONFIG, P_ROOT_NODE_NAME, serverList));
		}
		catch (ConfigurationException ce) {
			
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Unable to load the project configuration.", ce);
			}
			else {
				LOGGER.warn("Unable to load the project configuration due to: {}", ce.getMessage());
			}
		}

		try {
			config.addConfiguration(new YamlConfigurationFile(BASE_CONFIG_PATH, P_ROOT_NODE_NAME, serverList));
		}
		catch (ConfigurationException ce) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Unable to load the Probe Dock configuration.", ce);
			}
			else {
				LOGGER.warn("Unable to load the Probe Dock configuration due to: {}", ce.getMessage());
			}
		}

		if (!serverList.isEmpty()) {
			LOGGER.debug(getServerListDescription());
		}

		final ServerConfiguration server = getInternalServerConfiguration();
		if (config.getNumberOfConfigurations() == 0) {
			disabled = true;
		} else if (serverList.isEmpty()) {
			disabled = true;
			LOGGER.warn("No server is defined in the Probe Dock configuration files"
					+ "; define servers under the \"servers\" property.");
		} else if (server == null) {
			disabled = true;
			LOGGER.warn("No known server is selected in the Probe Dock configuration files"
					+ "; set the \"server\" property to the name of one of the configured servers.");
		} else if (!server.isValid()) {
			disabled = true;
			LOGGER.warn("The selected server ({}) in the Probe Dock configuration file is invalid", server.getName());
		}
	}
	
	/**
	 * @return The configuration instance
	 */
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		
		return instance;
	}
	
	/**
	 * Enforce the fact that a parameter is mandatory
	 *
	 * @param name The name of the parameter
	 * @return The value found
	 * @throws ConfigurationException When a mandatory parameter is missing
	 */
	private String getMandatory(String name) {
		if (!config.containsKey(name)) {
			throw new ProbeConfigurationException(name + " parameter is missing.");
		}
		else {
			return config.getString(name);
		}
		
	}
	
	/**
	 * @return In case of missing mandatory configuration, Probe Dock will be disabled to allow a smooth failure
	 */
	public boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * @return The home directory where Probe Dock client working files are stored
	 */
	public final String getWorkspace() {
		return getEnvironmentString("WORKSPACE", config.getString(P_WORKSPACE, DEFAULT_HOMEDIR)).replace("~",System.getProperty("user.home"));
	}
	
	/**
	 * @return Get the serializer class name to use when test results are serialized
	 */
	public String getSerializer() {
		return config.getString(P_SERIALIZER_CLASS);
	}

	public ServerConfiguration getServerConfiguration() {
		return getInternalServerConfiguration();
	}

	private ServerConfiguration getInternalServerConfiguration() {
		return serverList.get(getEnvironmentString("SERVER", config.getString(P_SERVER)));
	}
	
	private String getServerListDescription() {
		final StringBuilder builder = new StringBuilder("Probe Dock server list: ");

		for (final String serverName : serverList.getServerNames()) {
			if (builder.charAt(builder.length() - 1) != ' ') {
				builder.append(", ");
			}

			builder.append(serverName);

			if (!serverList.get(serverName).isValid()) {
				builder.append(" (INVALID)");
			} else if (serverName.equals(config.getString(P_SERVER))) {
				builder.append(" (selected)");
			}
		}

		return builder.toString();
	}

	/**
	 * @return the Probe Dock API identifier of the project
	 * @throws ConfigurationException if no project API identifier is set in the configuration files
	 */
	public String getProjectApiId() {
		final ServerConfiguration server = getInternalServerConfiguration();

		if (server != null && server.getProjectApiId() != null) {
			return server.getProjectApiId();
		}

		final String globalProjectApiId = config.getString(P_PROJECT_API_ID);

		if (globalProjectApiId == null) {
			throw new ProbeConfigurationException(
				"Probe Dock project API identifier is missing" +
				"; set the project.apiId property" +
				" or the projectApiId property of the selected server."
			);
		}

		return globalProjectApiId;
	}

	/**
	 * @return The project version
	 * @throws RuntimeException When there is no version configured
	 */
	public String getProjectVersion() {
		return getMandatory(P_PROJECT_VERSION);
	}

	/**
	 * @return The seed generator used in random generators
	 */
	public Long getGeneratorSeed() {
		if (config.containsKey(P_PROJECT_GENERATORSEED)) {
			return config.getLong(P_PROJECT_GENERATORSEED, null);
		}
		
		return config.getLong(P_GENERATORSEED, null);
	}
	
	/**
	 * @return By default, optimization will be done
	 */
	public boolean isPayloadCache() {
		// TODO: Refactor the caching mechanism
		return Boolean.FALSE; // getEnvironmentBoolean("CACHE_PAYLOAD", config.getBoolean(P_PAYLAOD_CACHE, Boolean.TRUE));
	}
	
	/**
	 * @return By default, no print will be done
	 */
	public boolean isPayloadPrint() {
		return getEnvironmentBoolean("PRINT_PAYLOAD", config.getBoolean(P_PAYLOAD_PRINT, Boolean.FALSE));
	}
	
	/**
	 * @return The class name to use for the optimization process
	 */
	public String getOptimizerStoreClass() {
		return config.getString(P_OPTIMIZER_CLASS);
	}
	
	/**
	 * @return The caching directory where to store cached data
	 */
	public String getOptimizerCacheDir() {
		return config.getString(P_OPTIMIZER_CACHE_DIR, DEFAULT_CACHEDIR).replace("~", System.getProperty("user.home"));
	}
	
	/**
	 * @return Get tags from the configuration, if none, empty set is returned
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getTags() {
		if (tags == null) {
			tags = new HashSet<>();
			
			List<String> globalTags = (List<String>) config.getProperty(P_TAGS);
			if (globalTags != null && !globalTags.isEmpty()) {
				tags.addAll(globalTags);
			}
			
			List<String> projectTags = (List<String>) config.getProperty(P_PROJECT_TAGS);
			if (projectTags != null && !projectTags.isEmpty()) {
				tags.addAll(projectTags);
			}
		}
		
		return tags;
	}

	/**
	 * @return Get ticket from the configuration, if none, empty set is returned
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getTickets() {
		if (tickets == null) {
			tickets = new HashSet<>();
			
			List<String> globalTickets = (List<String>) config.getProperty(P_TICKETS);
			if (globalTickets != null && !globalTickets.isEmpty()) {
				tickets.addAll(globalTickets);
			}
			
			List<String> projectTickets = (List<String>) config.getProperty(P_PROJECT_TICKETS); 
			if (projectTickets != null && !projectTickets.isEmpty()) {
				tickets.addAll(projectTickets);
			}
		}
		
		return tickets;
	}
	
	/**
	 * @return The category of the tests
	 */
	public String getCategory() {
		if (config.containsKey(P_PROJECT_CATEGORY)) {
			return config.getString(P_PROJECT_CATEGORY);
		}
		
		return config.getString(P_CATEGORY);
	}
	
	/**
	 * @return Define if the results must be stored or not locally
	 */
	public boolean isSave() {
		return getEnvironmentBoolean("SAVE_PAYLOAD", config.getBoolean(P_PAYLOAD_SAVE, Boolean.FALSE));
	}

	/**
	 * @return Define if the test results must be send to Probe Dock.
	 */
	public boolean isPublish() {
		return getEnvironmentBoolean("PUBLISH", config.getBoolean(P_PUBLISH, Boolean.TRUE));
	}

	/**
	 * Retrieve boolean value for the environment variable name
	 *
	 * @param name The name of the variable without prefix
	 * @param defaultValue The default value if not found
	 * @return The value found, or the default if not found
	 */
	private boolean getEnvironmentBoolean(String name, boolean defaultValue) {
		String value = getEnvironmentString(name, null);

		if (value == null) {
			return defaultValue;
		}
		else {
			return BOOLEAN_PATTERN.matcher(value).matches();
		}
	}

	/**
	 * Retrieve string value for the environment variable name
	 *
	 * @param name The name of the variable without prefix
	 * @param defaultValue The default value if not found
	 * @return The value found, or the default if not found
	 */
	private String getEnvironmentString(String name, String defaultValue) {
		return System.getenv(ENV_PREFIX + name) != null ? System.getenv(ENV_PREFIX + name) : defaultValue;
	}

	/**
	 * @return The current UID, null if none is available
	 */
	public String getCurrentUid() {
		return getEnvironmentString("TEST_REPORT_UID", readUid(new File(UID_FILE_NAME)));
	}

	/**
	 * Read a UID file
	 *
	 * @param uidFile The UID file to read
	 * @return The UID read
	 */
	private String readUid(File uidFile) {
		String uid = null;

		// Try to read the shared UID
		try (BufferedReader br = new BufferedReader(new FileReader(uidFile))){
			String line;

			while ((line = br.readLine()) != null) {
				uid = line;
			}
		}
		catch (IOException ioe) {}

		return uid;
	}
}

package io.probedock.client.common.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	/**
	 * Default home directory
	 */
	private static final String DEFAULT_HOMEDIR = System.getProperty("user.home") + "/.probe-dock";
	private static final String DEFAULT_CACHEDIR = DEFAULT_HOMEDIR + "/cache";
	
	/**
	 * Base configuration that should be present in the home directory
	 */
	private static final String BASE_CONFIG_PATH = ".probe-dock/config.yml";
	
	/**
	 * Configuration placed in the classpath
	 */
	private static final String CLASSPATH_CONFIG = "config.yml";
	
	/**
	 * The path to the UID directory into the home directory
	 */
	private static final String UID_DIRECTORY = "uid";
	
	/**
	 * Root node name of the tree configuration
	 */
	protected static final String P_ROOT_NODE_NAME = "probe-dock";
	
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
//	private static final String P_PROJECT_GROUP					= P_ROOT_NODE_NAME + ".project.group";
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
	
	/**
	 * The path to uid directory
	 */
	private File uidDirectory;
	
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

		// Ensure there is the base path for UID directory
		uidDirectory = new File(getWorkspace(), UID_DIRECTORY);
		if (uidDirectory.exists() && uidDirectory.isFile()) {
			throw new IllegalArgumentException("The UID file in the Probe Dock home directory is not a directory");
		}
		else if (!uidDirectory.exists()) {
			uidDirectory.mkdir();
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
		return config.getString(P_WORKSPACE, DEFAULT_HOMEDIR).replace("~",System.getProperty("user.home"));
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
		return serverList.get(config.getString(P_SERVER));
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

//	/**
//	 * @return The group (allow grouping multiple test runs in one group)
//	 */
//	public String getGroup() {
//		if (config.containsKey(P_PROJECT_GROUP)) {
//			return config.getString(P_PROJECT_GROUP);
//		}
//
//		return config.getString(P_GROUP);
//	}

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
		return Boolean.FALSE; // config.getBoolean(P_PAYLAOD_CACHE, Boolean.TRUE);
	}
	
	/**
	 * @return By default, no print will be done
	 */
	public boolean isPayloadPrint() {
		return config.getBoolean(P_PAYLOAD_PRINT, Boolean.FALSE);
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
		return config.getBoolean(P_PAYLOAD_SAVE, Boolean.FALSE);
	}

	/**
	 * @return Define if the test results must be send to Probe Dock.
	 */
	public boolean isPublish() {
		return config.getBoolean(P_PUBLISH, Boolean.TRUE);
	}
	
//	/**
//	 * Shortcut method to {@link Configuration#getUid(java.lang.String, java.lang.String, java.lang.String, boolean) }
//	 *
//	 * @param category The category
//	 * @param projectName The project name
//	 * @param projectVersion The project version
//	 * @return The UID generated
//	 */
//	public String getUid(String category, String projectName, String projectVersion) {
//		return getUid(category, projectName, projectVersion, false);
//	}
//
//	/**
//	 * Generate a UID or retrieve the latest if it is valid depending the context given
//	 * by the category, project name and project version
//	 *
//	 * @param category The category
//	 * @param projectName The project name
//	 * @param projectVersion The project version
//	 * @param force Force the generation of a new UID
//	 * @return The valid UID
//	 */
//	public String getUid(String category, String projectName, String projectVersion, boolean force) {
//		String uid = null;
//
//		if (!force) {
//			uid = readUid(new File(uidDirectory, "latest"));
//		}
//
//		// Check if the UID was already used for the Probe Dock client and project/version
//		if (uid != null && uidAlreadyUsed(category, projectName, uid)) {
//			uid = null;
//		}
//
//		// Generate UID and store it
//		if (uid == null) {
//			uid = generateUid();
//			writeUid(new File(uidDirectory, "latest"), uid);
//		}
//
//		writeUid(getUidFile(category, projectName, projectVersion), uid);
//
//		return uid;
//	}
//
//	/**
//	 * @return The last UID generated, null if none is available
//	 */
//	public String retrieveLastUid() {
//		return readUid(new File(uidDirectory, "latest"));
//	}
//
//	/**
//	 * @return Generate a UID
//	 */
//	private String generateUid() {
//		return UUID.randomUUID().toString();
//	}
//
//	/**
//	 * Read a UID file
//	 *
//	 * @param uidFile The UID file to read
//	 * @return The UID read
//	 */
//	private String readUid(File uidFile) {
//		String uid = null;
//
//		// Try to read the shared UID
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(uidFile));
//
//			String line;
//			while ((line = br.readLine()) != null) {
//				uid = line;
//			}
//		}
//		catch (IOException ioe) {}
//		finally { if (br != null) { try { br.close(); } catch (IOException ioe) {} } }
//
//		return uid;
//	}
//
//	/**
//	 * Write a UID file
//	 * @param uidFile The UID file to write
//	 * @param uid The UID to write to the file
//	 */
//	private void writeUid(File uidFile, String uid) {
//		BufferedWriter bw = null;
//		try {
//			bw = new BufferedWriter(new FileWriter(uidFile));
//			bw.write(uid);
//		}
//		catch (IOException ioe) {}
//		finally { if (bw != null) { try { bw.close(); } catch (IOException ioe) {} } }
//	}
//
//	/**
//	 * Check if a UID was already used in any context
//	 *
//	 * @param category The category to check
//	 * @param projectName The project name to check
//	 * @param uid The UID to validate
//	 * @return True if the UID is present for at least one version of a project in a category, false otherwise
//	 */
//	private boolean uidAlreadyUsed(String category, String projectName, String uid) {
//		if (uid == null) {
//			return false;
//		}
//
//		else {
//			for (File versionDirectory : getUidFilesForProject(category, projectName)) {
//				String uidRead = readUid(new File(versionDirectory, "latest"));
//
//				if (uidRead != null && !uidRead.isEmpty()) {
//					if (uidRead.equals(uid)) {
//						return true;
//					}
//				}
//			}
//
//			return false;
//		}
//	}
//
//	/**
//	 * Retrieve the list of the version directories for the project
//	 *
//	 * @param category The category
//	 * @param projectName The project name
//	 * @return The list of version directories or empty list if none are present
//	 */
//	private List<File> getUidFilesForProject(String category, String projectName) {
//		// Check that the category directory exists
//		File categoryDirectory = new File(uidDirectory, category);
//		if (!categoryDirectory.exists() || (categoryDirectory.exists() && categoryDirectory.isFile())) {
//			return new ArrayList<>();
//		}
//
//		// Check that the project directory exists
//		File projectDirectory = new File(categoryDirectory, projectName);
//		if (!projectDirectory.exists() || (projectDirectory.exists() && projectDirectory.isFile())) {
//			return new ArrayList<>();
//		}
//
//		// Get all the version directories
//		List<File> versionDirectories = new ArrayList<>();
//		for (File electableDirectory : projectDirectory.listFiles()) {
//			if (electableDirectory.isDirectory()) {
//				versionDirectories.add(electableDirectory);
//			}
//		}
//
//		return versionDirectories;
//	}
//
//	/**
//	 * Get the UID file regarding the category, project name and project version
//	 *
//	 * @param category The category
//	 * @param projectName The project name
//	 * @param projectVersion The project version
//	 * @return The UID file
//	 */
//	private File getUidFile(String category, String projectName, String projectVersion) {
//		// Check that the category directory exists
//		File categoryDirectory = new File(uidDirectory, category);
//		if (categoryDirectory.exists() && categoryDirectory.isFile()) {
//			throw new IllegalArgumentException("The category file for the UID storage is not a directory");
//		}
//		else if (!categoryDirectory.exists()) {
//			categoryDirectory.mkdir();
//		}
//
//		// Check that the project directory exists
//		File projectDirectory = new File(categoryDirectory, projectName);
//		if (projectDirectory.exists() && projectDirectory.isFile()) {
//			throw new IllegalArgumentException("The project file for the UID store is not a directory");
//		}
//		else if (!projectDirectory.exists()) {
//			projectDirectory.mkdir();
//		}
//
//		// Check that the version directory exists
//		File versionDirectory = new File(projectDirectory, projectVersion);
//		if (versionDirectory.exists() && versionDirectory.isFile()) {
//			throw new IllegalArgumentException("The version file for the UID store is not a directory");
//		}
//		else if (!versionDirectory.exists()) {
//			versionDirectory.mkdir();
//		}
//
//		return new File(versionDirectory, "latest");
//	}
}

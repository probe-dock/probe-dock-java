package io.probedock.client.core.storage;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.common.utils.Constants;
import io.probedock.client.core.serializer.ProbeSerializer;
import io.probedock.client.core.serializer.json.JsonSerializer;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File store to keep the result between runs
 * 
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class FileStore {
	private static final Logger LOGGER = Logger.getLogger(FileStore.class.getCanonicalName());
	
	private Configuration configuration;
	
	private ProbeSerializer serializer;
	
	/**
	 * Constructor
	 * 
	 * @param configuration The configuration
	 */
	public FileStore(Configuration configuration) {
		this.configuration = configuration;
		
		if (configuration.getSerializer() == null) {
			LOGGER.info("Default serializer " + JsonSerializer.class.getName() + " will be used.");
			serializer = new JsonSerializer();
		}
		else {
			try {
				serializer = (ProbeSerializer) getClass().getClassLoader().loadClass(configuration.getSerializer()).newInstance();
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.log(Level.WARNING, "Unable to create the serializer " + configuration.getSerializer() + ", default one will be used.", e);
			}
			finally {
				serializer = new JsonSerializer();
			}
		}
	}
	
	/**
	 * Save a payload
	 * 
	 * @param probeTestRun The payload to save
	 * @throws IOException I/O Errors
	 */
	public void save(ProbeTestRun probeTestRun) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(
			new FileOutputStream(new File(getTmpDir(probeTestRun), UUID.randomUUID().toString())),
			Charset.forName(Constants.ENCODING).newEncoder()
		);
		
		serializer.serializePayload(osw, probeTestRun, true);
	}
	
	/**
	 * Load a payload
	 * 
	 * @param <T> The payload type
	 * @param name The name of the payload file to load
	 * @param clazz The class of the payload
	 * @return The payload loaded
	 * @throws IOException I/O Errors
	 */
	public <T extends ProbeTestRun> T load(String name, Class<T> clazz) throws IOException {
		InputStreamReader isr = new InputStreamReader(
			new FileInputStream(new File(getTmpDir(clazz), name)),
			Charset.forName(Constants.ENCODING).newDecoder()
		);
		
		return serializer.deserializePayload(isr, clazz);
	}
	
	/**
	 * Load a list of payload
	 * 
	 * @param <T> The payload type
	 * @param clazz The class of the payload
	 * @return The list of payloads loaded
	 * @throws IOException 
	 */
	public <T extends ProbeTestRun> List<T> load(Class<T> clazz) throws IOException {
		List<T> payloads = new ArrayList<>();
		
		for (File f : getTmpDir(clazz).listFiles()) {
			if (f.isFile()) {
				InputStreamReader isr = new InputStreamReader(
					new FileInputStream(f),
					Charset.forName(Constants.ENCODING).newDecoder()
				);

				payloads.add(serializer.deserializePayload(isr, clazz));
			}
		}
		
		return payloads;
	}
	
	/**
	 * Clear the directory where payloads are stored
	 * 
	 * @param clazz The class of the payload
	 * @throws IOException I/O Errors
	 */
	public void clear(Class<? extends ProbeTestRun> clazz) throws IOException {
		FileUtils.cleanDirectory(getTmpDir(clazz));
	}
	
	/**
	 * Retrieve the temporary directory where to store/load payloads
	 * 
	 * @param testRun The test run to retrieve the temp directory
	 * @return The temp directory, new one if the directory does not exist
	 */
	private File getTmpDir(ProbeTestRun testRun) {
		File tmpDir = new File(configuration.getWorkspace() + "/tmp/" + testRun.getVersion());
		
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		
		return tmpDir;
	}
	
	/**
	 * Retrieve the temporary directory where to store/load payloads
	 * 
	 * @param clazz The class of the payload to load
	 * @return The temp directory, null if not able to create a new instance of the payload class
	 */
	private File getTmpDir(Class<? extends ProbeTestRun> clazz) {
		try {
			return getTmpDir(clazz.newInstance());
		}
		catch (IllegalAccessException | InstantiationException iae) {
			return null;
		}
	}
}

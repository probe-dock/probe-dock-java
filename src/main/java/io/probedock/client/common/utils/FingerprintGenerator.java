package io.probedock.client.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Footprint utility to generate footprints from strings
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class FingerprintGenerator {
	private static final Logger LOGGER = Logger.getLogger(FingerprintGenerator.class.getCanonicalName());

	/**
	 * Generate a fingerprint for a given string
	 *
	 * @param str The string to fingerprint
	 * @return The fingerprint generated
	 */
	public static String fingerprint(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return byteArrayToHexString(md.digest(str.getBytes()));
		}
		catch (NoSuchAlgorithmException nsae) {
			LOGGER.warning("Unable to calculate the fingerprint for string [" + str + "].");
			return null;
		}
	}

	/**
	 * Generate a fingerprint based on package, class and method name
	 *
	 * @param packageName The package name
	 * @param className The class name
	 * @param methodName The method name
	 * @return The fingerprint generated
	 */
	public static String fingerprint(String packageName, String className, String methodName) {
		return fingerprint(packageName + "." + className + "." + methodName);
	}
	
	/**
	 * Convert a byte array to hexadecimal string
	 * 
	 * @param byteArray The byte array to convert
	 * @return The hexadecimal string
	 */
	private static String byteArrayToHexString(byte[] byteArray) {
		StringBuilder result = new StringBuilder();
		
		for (int i=0; i < byteArray.length; i++) {
			result.append(Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return result.toString();
	}
}

package io.probedock.client.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Footprint utility to generate footprints from strings
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class FootprintGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(FootprintGenerator.class);
	
	/**
	 * Calculate a footprint of a string
	 * 
	 * @param stringToFootprint The string for which the footprint is calculated
	 * @return The footprint calculated
	 */
	public static String footprint(String stringToFootprint) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return byteArrayToHexString(md.digest(stringToFootprint.getBytes()));
		}
		catch (NoSuchAlgorithmException nsae) {
			LOGGER.warn("Unable to calculate the footprint for string [{}].", stringToFootprint);
			return null;
		}	
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

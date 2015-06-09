package io.probedock.client.common.utils;

import java.lang.reflect.Method;

/**
 * Inflector to compute human names from package, class and 
 * method names.
 * 
 * @author Laurent Prévost <laurent.prevost@probedock.io>
 */
public class Inflector {
	/**
	 * Create a human name from a method
	 * 
	 * @param method The method to get a human name
	 * @return The human name created
	 */
	public static String getHumanName(Method method) {
		return getHumanName(method.getName());
	}
	
	/**
	 * Create a human name from a method name
	 * 
	 * @param methodName The method name to get a human name
	 * @return The human name created
	 */
	public static String getHumanName(String methodName) {
		char[] name = methodName.toCharArray();
		StringBuilder humanName = new StringBuilder();
		
		boolean digit = false;
		boolean upper = true;
		
		int upCount = 0;
		
		for (int i = 0; i < name.length; i++) {
			if (i == 0) {
				humanName.append(Character.toUpperCase(name[i]));
			}
			else {
				humanName.append(Character.toLowerCase(name[i]));
			}
			
			if (i < name.length - 1) {
				if (!digit && Character.isDigit(name[i + 1])) {
					digit = true;
					humanName.append(" ");
				}

				else if (digit && !Character.isDigit(name[i + 1])) {
					digit = false;
					humanName.append(" ");
				}

				else if (upper && !Character.isUpperCase(name[i + 1])) {
					if (upCount == 2) {
						humanName.insert(humanName.length() - 2, " ");
					}
					
					upper = false;
					upCount = 0;
					
					humanName.insert(humanName.length() - 1, " ");
				}
				
				else if (Character.isUpperCase(name[i + 1])) {
					upCount++;
					upper = true;
				}
			}
		}

		return humanName.toString().replaceAll("^ ", "");
	}
}

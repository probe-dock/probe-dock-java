package io.probedock.client.test.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Method utilities to test the Rox 
 * 
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class TestHelper {
	/**
	 * Allow to set a static attribute with a custom value
	 * @param field The attribute to modify
	 * @param newValue The new value to set
	 * @throws Exception Any exception that occurs
	 */
	public static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		// remove final modifier from field
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}		
}

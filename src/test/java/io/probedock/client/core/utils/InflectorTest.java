package io.probedock.client.core.utils;

import io.probedock.client.common.utils.Inflector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * TestResult for class {@link Inflector}
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class InflectorTest {
	@Test
	public void inflectorShouldReturnCorrectTransformation() {
		assertEquals("The name theMethodNameShouldBeTransformed was not humanized", 
			Inflector.getHumanName("theMethodNameShouldBeTransformed"), 
			"The method name should be transformed");
	}
	
	@Test
	public void inflectorShouldReturnCorrectTransformationFromMethodName() {
		try {
			assertEquals("The name theMethodNameShouldBeTransformed was not humanized", 
				Inflector.getHumanName(InflectorTest.class.getMethod("inflectorShouldReturnCorrectTransformationFromMethodName")), 
				"Inflector should return correct transformation from method name");
		}
		catch (NoSuchMethodException nme) {
			fail(nme.getMessage());
		}
		catch (SecurityException se) {
			fail(se.getMessage());
		}
	}
	
	@Test
	public void inflectorShouldWorkWhenNumbersArePresentInTheMethodName() {
		assertEquals("The name methodWithNumber1AndNumber2PresentInTheMethodName was not humanized", 
			Inflector.getHumanName("methodWithNumber1AndNumber2PresentInTheMethodName"), 
			"Method with number 1 and number 2 present in the method name");
	}
}

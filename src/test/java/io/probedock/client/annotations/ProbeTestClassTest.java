package io.probedock.client.annotations;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TestResult class for {@link ProbeTestClass}
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class ProbeTestClassTest {
	private ProbeTestClass annotation;
	
	@Before
	public void setUp() {
		try {
			annotation = DummyClass.class.getAnnotation(ProbeTestClass.class);
		}
		catch (Exception e) {}
	}
	
	@Test
	public void probeClassAnnotationShouldHaveEmptyValueForCategoryByDefault() {
		assertEquals("The default value for rox category should be empty", "", annotation.category());
	}
	
	@Test
	public void probeClassAnnotationShouldHaveEmptyArrayOfStringForTagsByDefault() {
		assertEquals("The default value for rox tags should be empty array", 0, annotation.tags().length);
	}

	@Test
	public void probeClassAnnotationShouldHaveEmptyArrayOfStringForTicketsByDefault() {
		assertEquals("The default value for rox tickets should be empty array", 0, annotation.tickets().length);
	}

	@ProbeTestClass
	public class DummyClass {}
}

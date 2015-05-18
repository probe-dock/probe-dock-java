package io.probedock.client.annotations;

import io.probedock.client.utils.CollectionHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestResult class for {@link CollectionHelper}
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class ProbeTestTest {
	private ProbeTest annotation;
	
	@ProbeTest(key = "")
	public void dummyMethod() {}
	
	@Before
	public void setUp() {
		try {
			annotation = ProbeTestTest.class.getMethod("dummyMethod").getAnnotation(ProbeTest.class);
		}
		catch (Exception e) {}
	}
	
	@Test
	public void probeMethodAnnotationShouldNotAllowNullKey() {
		assertNotNull("The key on annotation cannot be null", annotation.key());
	}
	
	@Test
	public void probeMethodAnnotationShouldHaveEmptyValueForCategoryByDefault() {
		assertEquals("The default value for rox category should be empty", "", annotation.category());
	}
	
	@Test
	public void probeMethodAnnotationShouldHaveEmptyArrayOfStringForTagsByDefault() {
		assertEquals("The default value for rox tags should be empty array", 0, annotation.tags().length);
	}

	@Test
	public void probeMethodAnnotationShouldHaveEmptyArrayOfStringForTicketsByDefault() {
		assertEquals("The default value for rox tickets should be empty array", 0, annotation.tickets().length);
	}
	
	@Test
	public void probeMethodAnnotationShouldBeActiveByDefault() {
		assertTrue("Must be active by default", annotation.active());
	}
}

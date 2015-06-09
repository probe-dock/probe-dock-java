package io.probedock.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enrich the information for a test. The information
 * added to the test methods are used to send the test results to
 * ProbeDock.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProbeTest {
	/**
	 * Unique key that identifies a test in ProbeDock. The key
	 * is optional and can be arbitrary set or generated through
	 * ProbeDock.
	 */
	String key() default "";

	/**
	 * Descriptive name to override the name of a test manually
	 */
	String name() default "";
	
	/**
	 * Category of test: Ex: unit, selenium or integration 
	 */
	String category() default "";

	/**
	 * Contributors of the test: Ex: someone@somewhere.localdomain
	 */
	String[] contributors() default {};

	/**
	 * Tags for a test
	 */
	String[] tags() default {};

	/**
	 * Tickets that refers to a ticketing system
	 */
	String[] tickets() default {};

	boolean active() default true;
}

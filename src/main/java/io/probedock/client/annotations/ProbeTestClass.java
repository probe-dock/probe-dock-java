package io.probedock.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To apply some information across multiple tests
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProbeTestClass {
	/**
	 * Category
	 */
	String category() default "";

	/**
	 * Tags to flag a test
	 */
	String[] tags() default {};
	
	/**
	 * Tickets that refers to a ticketing system
	 */
	String[] tickets() default {};
}

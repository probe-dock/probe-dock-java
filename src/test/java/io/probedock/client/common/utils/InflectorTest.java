package io.probedock.client.common.utils;

import io.probedock.client.annotations.ProbeTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test for class {@link Inflector}
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class InflectorTest {
    @ProbeTest(name = "This is a custom test name")
    public void dummyMethod() {}

    private ProbeTest annotation;

    @Before
    public void setUp() {
        try {
            annotation = InflectorTest.class.getMethod("dummyMethod").getAnnotation(ProbeTest.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethod() throws Exception {
        assertEquals("The name theMethodNameShouldBeTransformed was not humanized",
            Inflector.forgeName(InflectorTest.class, InflectorTest.class.getDeclaredMethod("inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethod"), null),
            "Inflector test: inflector should return correct forged name based on class and method");
    }

    @Test
    public void inflectorShouldReturnCorrectForgedNameBasedOnClassMethodAndAnnotation() throws Exception {
        assertEquals("The name theMethodNameShouldBeTransformed was not humanized",
            Inflector.forgeName(InflectorTest.class, InflectorTest.class.getDeclaredMethod("inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethod"), annotation),
            "This is a custom test name");
    }

    @Test
    public void inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethodName() throws Exception {
        String methodName = InflectorTest.class.getDeclaredMethod("inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethod").getName();

        assertEquals("The name theMethodNameShouldBeTransformed was not humanized",
            Inflector.forgeName(InflectorTest.class, methodName, null),
            "Inflector test: inflector should return correct forged name based on class and method");
    }

    @Test
    public void inflectorShouldReturnCorrectForgedNameBasedOnClassMethodNameAndAnnotation() throws Exception {
        String methodName = InflectorTest.class.getDeclaredMethod("inflectorShouldReturnCorrectForgedNameBasedOnClassAndMethod").getName();

        assertEquals("The name theMethodNameShouldBeTransformed was not humanized",
            Inflector.forgeName(InflectorTest.class, methodName, annotation),
            "This is a custom test name");
    }

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
        } catch (NoSuchMethodException nme) {
            fail(nme.getMessage());
        } catch (SecurityException se) {
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

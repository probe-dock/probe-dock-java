package io.probedock.client.common.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import io.probedock.client.common.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for class {@link Inflector}
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class TestResultDataUtilsTest {
    @ProbeTestClass(
        category = "classCategory",
        contributors = { "a@localhost.localdomain", "b@localhost.localdomain" },
        tags = { "ta1", "ta2" },
        tickets = { "ti1", "ti2" }
    )
    private class DummyClassWithClassAnnotation {
    }

    @ProbeTestClass
    private class DummyClassWithClassAnnotationWithoutData {
    }

    @ProbeTest(
        name = "This is a custom test name",
        key = "123",
        active = true,
        category = "methodCategory",
        contributors = { "c@localhost.localdomain", "d@localhost.localdomain" },
        tags = { "ta3", "ta4" },
        tickets = { "ti3", "ti4" }
    )
    public void dummyMethod() {
    }

    @ProbeTest
    public void dummyMethodWitEmptyAnnotation() {
    }

    private ProbeTest methodAnnotation;
    private ProbeTest methodAnnotationWithEmptyData;
    private ProbeTestClass classAnnotation;
    private ProbeTestClass classAnnotationWithEmptyData;

    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        try {
            methodAnnotation = TestResultDataUtilsTest.class.getMethod("dummyMethod").getAnnotation(ProbeTest.class);
            methodAnnotationWithEmptyData = TestResultDataUtilsTest.class.getMethod("dummyMethodWitEmptyAnnotation").getAnnotation(ProbeTest.class);
            classAnnotation = DummyClassWithClassAnnotation.class.getAnnotation(ProbeTestClass.class);
            classAnnotationWithEmptyData = DummyClassWithClassAnnotationWithoutData.class.getAnnotation(ProbeTestClass.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void itShouldBePossibleToRetrieveTestKeyWhenAnnotationIsPresent() {
        assertEquals("123", TestResultDataUtils.getKey(methodAnnotation));
    }

    @Test
    public void itShouldBePossibleToRetrieveNullTestKeyWhenAnnotationWithoutDataIsPresent() {
        assertNull(TestResultDataUtils.getKey(methodAnnotationWithEmptyData));
    }

    @Test
    public void itShouldBePossibleToRetrieveNullTestKeyWhenAnnotationIsNotPresent() {
        assertNull(TestResultDataUtils.getKey(null));
    }

    @Test
    public void itShouldBePossibleToRetrieveActiveStatusWhenAnnotationIsPresent() {
        assertTrue(TestResultDataUtils.isActive(methodAnnotation));
    }

    @Test
    public void itShouldBePossibleToRetrieveTrueActiveStatusWhenAnnotationWithoutDataIsPresent() {
        assertTrue(TestResultDataUtils.isActive(methodAnnotationWithEmptyData));
    }

    @Test
    public void itShouldBePossibleToRetrieveNullActiveStatusKeyWhenAnnotationIsNotPresent() {
        assertNull(TestResultDataUtils.isActive(null));
    }

    @Test
    public void itShouldBePossibleToRetrieveMethodCategoryWhenAvailable() {
        when(configuration.getCategory()).thenReturn(null);
        assertEquals("methodCategory", TestResultDataUtils.getCategory(configuration, classAnnotationWithEmptyData, methodAnnotation, "defaultCategory"));
    }

    @Test
    public void itShouldBePossibleToRetrieveClassCategoryWhenAvailable() {
        when(configuration.getCategory()).thenReturn(null);
        assertEquals("classCategory", TestResultDataUtils.getCategory(configuration, classAnnotation, methodAnnotationWithEmptyData, "defaultCategory"));
    }

    @Test
    public void itShouldBePossibleToRetrieveConfigurationCategoryWhenAvailable() {
        when(configuration.getCategory()).thenReturn("configurationCategory");
        assertEquals("configurationCategory", TestResultDataUtils.getCategory(configuration, classAnnotationWithEmptyData, methodAnnotationWithEmptyData, "defaultCategory"));
    }

    @Test
    public void itShouldBePossibleToRetrieveDefaultCategoryWhenAvailable() {
        when(configuration.getCategory()).thenReturn(null);
        assertEquals("defaultCategory", TestResultDataUtils.getCategory(configuration, classAnnotationWithEmptyData, methodAnnotationWithEmptyData, "defaultCategory"));
    }

    @Test
    public void itShouldBePossibleToRetrieveMethodContributorsWhenAvailable() {
        when(configuration.getContributors()).thenReturn(new HashSet<String>());

        String[] contributors = TestResultDataUtils.getContributors(configuration, classAnnotationWithEmptyData, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "c@localhost.localdomain", "d@localhost.localdomain" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveClassContributorsWhenAvailable() {
        when(configuration.getContributors()).thenReturn(new HashSet<String>());

        String[] contributors = TestResultDataUtils.getContributors(configuration, classAnnotation, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "a@localhost.localdomain", "b@localhost.localdomain" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveConfigurationContributorsWhenAvailable() {
        when(configuration.getContributors()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "e@localhost.localdomain", "f@localhost.localdomain" })));

        String[] contributors = TestResultDataUtils.getContributors(configuration, classAnnotationWithEmptyData, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "e@localhost.localdomain", "f@localhost.localdomain" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveAllContributors() {
        when(configuration.getContributors()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "e@localhost.localdomain", "f@localhost.localdomain" })));

        String[] contributors = TestResultDataUtils.getContributors(configuration, classAnnotation, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "a@localhost.localdomain", "b@localhost.localdomain", "c@localhost.localdomain", "d@localhost.localdomain", "e@localhost.localdomain", "f@localhost.localdomain" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveMethodTagsWhenAvailable() {
        when(configuration.getTags()).thenReturn(new HashSet<String>());

        String[] tags = TestResultDataUtils.getTags(configuration, classAnnotationWithEmptyData, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "ta3", "ta4" };

        Arrays.sort(tags);
        Arrays.sort(result);

        assertArrayEquals(result, tags);
    }

    @Test
    public void itShouldBePossibleToRetrieveClassTagsWhenAvailable() {
        when(configuration.getTags()).thenReturn(new HashSet<String>());

        String[] tags = TestResultDataUtils.getTags(configuration, classAnnotation, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "ta1", "ta2" };

        Arrays.sort(tags);
        Arrays.sort(result);

        assertArrayEquals(result, tags);
    }

    @Test
    public void itShouldBePossibleToRetrieveConfigurationTagsWhenAvailable() {
        when(configuration.getTags()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "ta5", "ta6" })));

        String[] contributors = TestResultDataUtils.getTags(configuration, classAnnotationWithEmptyData, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "ta5", "ta6" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveAllTags() {
        when(configuration.getTags()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "ta5", "ta6" })));

        String[] contributors = TestResultDataUtils.getTags(configuration, classAnnotation, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "ta1", "ta2", "ta3", "ta4", "ta5", "ta6" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveMethodTicketsWhenAvailable() {
        when(configuration.getTickets()).thenReturn(new HashSet<String>());

        String[] tags = TestResultDataUtils.getTickets(configuration, classAnnotationWithEmptyData, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "ti3", "ti4" };

        Arrays.sort(tags);
        Arrays.sort(result);

        assertArrayEquals(result, tags);
    }

    @Test
    public void itShouldBePossibleToRetrieveClassTicketsWhenAvailable() {
        when(configuration.getTickets()).thenReturn(new HashSet<String>());

        String[] tags = TestResultDataUtils.getTickets(configuration, classAnnotation, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "ti1", "ti2" };

        Arrays.sort(tags);
        Arrays.sort(result);

        assertArrayEquals(result, tags);
    }

    @Test
    public void itShouldBePossibleToRetrieveConfigurationTicketsWhenAvailable() {
        when(configuration.getTickets()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "ti5", "ti6" })));

        String[] contributors = TestResultDataUtils.getTickets(configuration, classAnnotationWithEmptyData, methodAnnotationWithEmptyData).toArray(new String[] {});
        String[] result = new String[] { "ti5", "ti6" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveAllTickets() {
        when(configuration.getTickets()).thenReturn(new HashSet<>(Arrays.asList(new String[] { "ti5", "ti6" })));

        String[] contributors = TestResultDataUtils.getTickets(configuration, classAnnotation, methodAnnotation).toArray(new String[] {});
        String[] result = new String[] { "ti1", "ti2", "ti3", "ti4", "ti5", "ti6" };

        Arrays.sort(contributors);
        Arrays.sort(result);

        assertArrayEquals(result, contributors);
    }

    @Test
    public void itShouldBePossibleToRetrieveTechnicalNameFromTestClassAndMethodName() {
        assertEquals("DummyClassWithClassAnnotation.test", TestResultDataUtils.getTechnicalName(DummyClassWithClassAnnotation.class, "test"));
    }

    @Test
    public void itShouldBePossibleToRetrieveFingerprintFromTestClassAndMethodName() {
        assertEquals("54c989f2d1a8e64ab05695d1b0d887f4179c9b1a", TestResultDataUtils.getFingerprint(DummyClassWithClassAnnotation.class, "test"));
    }
}

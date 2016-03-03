package io.probedock.client.common.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import io.probedock.client.common.config.Configuration;
import io.probedock.client.utils.CollectionHelper;

import java.util.Map;
import java.util.Set;

/**
 * Utilities to extract various data from the test and annotations to send test results.
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class TestResultDataUtils {
    /**
     * Retrieve the key from the annotations
     *
     * @param annotation The test annotation
     * @return The key or null if there is no key
     */
    public static String getKey(ProbeTest annotation) {
        return annotation != null && annotation.key() != null && !annotation.key().isEmpty() ? annotation.key() : null;
    }

    /**
     * Check if a test is active or not
     *
     * @param annotation The test annotation
     * @return True/false if the the status is specified, otherwise, it will return null
     */
    public static Boolean isActive(ProbeTest annotation) {
        return annotation != null ? annotation.active() : null;
    }

    /**
     * Retrieve the category to apply to the test
     *
     * @param configuration The configuration to check if a category is provided
     * @param classAnnotation The class annotation to get the override category
     * @param methodAnnotation The method annotation to get the override category
     * @param defaultCategory The default category if none is found
     * @return The category found
     */
    public static String getCategory(Configuration configuration, ProbeTestClass classAnnotation, ProbeTest methodAnnotation, String defaultCategory) {
        return getCategory(null, configuration, classAnnotation, methodAnnotation, defaultCategory);
    }

    /**
     * Retrieve the category to apply to the test
     *
     * @param packageName The package name to match for the category
     * @param configuration The configuration to check if a category is provided
     * @param classAnnotation The class annotation to get the override category
     * @param methodAnnotation The method annotation to get the override category
     * @param defaultCategory The default category if none is found
     * @return The category found
     */
    public static String getCategory(String packageName, Configuration configuration, ProbeTestClass classAnnotation, ProbeTest methodAnnotation, String defaultCategory) {
        if (methodAnnotation != null && methodAnnotation.category() != null && !methodAnnotation.category().isEmpty()) {
            return methodAnnotation.category();
        }
        else if (classAnnotation != null && classAnnotation.category() != null && !classAnnotation.category().isEmpty()) {
            return classAnnotation.category();
        }
        else {
            // Try to match against patterns configured
            Map.Entry<String, String> match = PackageMatcher.match(configuration.getCategoriesByPackage(), packageName);

            // A category is found for the pattern
            if (match != null) {
                return match.getValue();
            }

            // Fallback on the category configured in the file
            else if (configuration.getCategory() != null && !configuration.getCategory().isEmpty()) {
                return configuration.getCategory();
            }

            // Fallback on the default category
            else {
                return defaultCategory;
            }
        }
    }

    /**
     * Compute the list of contributors associated for a test
     *
     * @param configuration The configuration to check if a category is provided
     * @param classAnnotation The class annotation to get info
     * @param methodAnnotation The method annotation to get info
     * @return The contributors associated to the test
     */
    public static Set<String> getContributors(Configuration configuration, ProbeTestClass classAnnotation, ProbeTest methodAnnotation) {
        return CollectionHelper.getContributors(configuration.getContributors(), methodAnnotation, classAnnotation);
    }

    /**
     * Compute the list of tags associated for a test
     *
     * @param configuration The configuration to check if a category is provided
     * @param classAnnotation The class annotation to get info
     * @param methodAnnotation The method annotation to get info
     * @return The tags associated to the test
     */
    public static Set<String> getTags(Configuration configuration, ProbeTestClass classAnnotation, ProbeTest methodAnnotation) {
        return CollectionHelper.getTags(configuration.getTags(), methodAnnotation, classAnnotation);
    }

    /**
     * Compute the list of tickets associated for a test
     *
     * @param configuration The configuration to check if a category is provided
     * @param classAnnotation The class annotation to get info
     * @param methodAnnotation The method annotation to get info
     * @return The tickets associated to the test
     */
    public static Set<String> getTickets(Configuration configuration, ProbeTestClass classAnnotation, ProbeTest methodAnnotation) {
        return CollectionHelper.getTickets(configuration.getTickets(), methodAnnotation, classAnnotation);
    }


    /**
     * Build the technical name
     *
     * @param testClass The test class
     * @param methodName The method name
     * @return The technical name
     */
    public static String getTechnicalName(Class testClass, String methodName) {
        return testClass.getSimpleName() + "." + methodName;
    }

    /**
     * Calculate a fingerprint for a test
     *
     * @param testClass The test class
     * @param methodName The method name
     * @return The fingerprint calculated
     */
    public static String getFingerprint(Class testClass, String methodName) {
        return FingerprintGenerator.fingerprint(testClass, methodName);
    }
}

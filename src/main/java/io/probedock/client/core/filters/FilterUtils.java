package io.probedock.client.core.filters;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import io.probedock.client.common.utils.FingerprintGenerator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Filter;

/**
 * Filter utility
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class FilterUtils {
	/**
	 * Define if a test is runnable or not based on a method name and class
	 * 
	 * @param cl Class
	 * @param methodName The method name
	 * @param filters The filters to apply
	 * @return True if the test can be run
	 */
	@SuppressWarnings("unchecked")
	public static boolean isRunnable(Class cl, String methodName, List<FilterDefinition> filters) {
		try {
			Method m = cl.getMethod(methodName);

			// Get the ROX annotations
			ProbeTest mAnnotation = m.getAnnotation(ProbeTest.class);
			ProbeTestClass cAnnotation = m.getDeclaringClass().getAnnotation(ProbeTestClass.class);

			String fingerprint = FingerprintGenerator.fingerprint(cl, m);

			if (mAnnotation != null || cAnnotation != null) {
				return isRunnable(new FilterTargetData(fingerprint, m, mAnnotation, cAnnotation), filters);
			}
			else {
				return isRunnable(new FilterTargetData(fingerprint, m), filters);
			}
		}
		catch (NoSuchMethodException | SecurityException e) {
			return true;
		}
	}

	/**
	 * Define if a test is runnable based on its description data
	 *
	 * @param fingerprint The fingerprint
	 * @param name TestResult name
	 * @param technicalName Technical name
	 * @param key ROX key
	 * @param tags The tags
	 * @param tickets The tickets
	 * @param filters The filters
	 * @return True if the test can be run
	 */
	public static boolean isRunnable(String fingerprint, String name, String technicalName, String key, String tags, String tickets, List<FilterDefinition> filters) {
		return isRunnable(new FilterTargetData(fingerprint, tags, tickets, technicalName, name, key), filters);
	}
	
	/**
	 * Define if a test is runnable based on the object that represents the test
	 * meta data
	 * 
	 * @param targetData The meta data
	 * @param filters The filters
	 * @return True if the test can be run
	 */
	private static boolean isRunnable(FilterTargetData targetData, List<FilterDefinition> filters) {
		if (filters == null || filters.isEmpty()) {
			return true;
		}

		for (FilterDefinition filterDefinition : filters) {
			String type = filterDefinition.getType();
			String text = filterDefinition.getText();

			if (type == null || type.isEmpty()) {
				type = "*";
			}

			// We must evaluate all the filters and return only when there is a valid match
			if (
				(type.equalsIgnoreCase("*") && targetData.anyMatch(text)) || // Any filter
				("key".equalsIgnoreCase(type) && targetData.keyMatch(text)) || // Key filter
				("fp".equalsIgnoreCase(type) && targetData.fingerpringMatch(text)) || // Fingerprint filter
				("name".equalsIgnoreCase(type) && targetData.nameMatch(text)) || // Name filter
				("tag".equalsIgnoreCase(type) && targetData.tagMatch(text)) || // Tag filter
				("ticket".equalsIgnoreCase(type) && targetData.ticketMatch(text)) // Ticket filter
			) {
				return true;
			}
		}

		return false;
	}
}
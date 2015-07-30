package io.probedock.client.common.utils;

import io.probedock.client.annotations.ProbeTest;

import java.lang.reflect.Method;

/**
 * Inflector to compute human names from package, class and
 * method names.
 *
 * @author Laurent Prévost <laurent.prevost@probedock.io>
 */
public class Inflector {
    /**
     * Forge a name from a class and a method. If an annotation is provided, then the method
     * content is used.
     *
     * @param cl Class to get the name
     * @param m Method to get the name
     * @param methodAnnotation The method annotation to override the normal forged name
     * @return The name forge and humanize
     */
    public static String forgeName(Class cl, Method m, ProbeTest methodAnnotation) {
        return forgeName(cl, m.getName(), methodAnnotation);
    }

    /**
     * Forge a name from a class and a method. If an annotation is provided, then the method
     * content is used.
     *
     * @param cl Class to get the name
     * @param methodName The method name
     * @param methodAnnotation The method annotation to override the normal forged name
     * @return The name forge and humanize
     */
    public static String forgeName(Class cl, String methodName, ProbeTest methodAnnotation) {
        if (methodAnnotation != null && !"".equalsIgnoreCase(methodAnnotation.name())) {
            return methodAnnotation.name();
        } else {
            return getHumanName(cl.getSimpleName() + ": " + methodName);
        }
    }

    /**
     * Create a human name from a method
     *
     * @param method The method to get a human name
     * @return The human name created
     */
    public static String getHumanName(Method method) {
        return getHumanName(method.getName());
    }

    /**
     * Create a human name from a method name
     *
     * @param methodName The method name to get a human name
     * @return The human name created
     */
    public static String getHumanName(String methodName) {
        char[] name = methodName.toCharArray();
        StringBuilder humanName = new StringBuilder();

        boolean digit = false;
        boolean upper = true;

        int upCount = 0;

        for (int i = 0; i < name.length; i++) {
            if (i == 0) {
                humanName.append(Character.toUpperCase(name[i]));
            } else {
                humanName.append(Character.toLowerCase(name[i]));
            }

            if (i < name.length - 1) {
                if (!digit && Character.isDigit(name[i + 1])) {
                    digit = true;
                    humanName.append(" ");
                } else if (digit && !Character.isDigit(name[i + 1])) {
                    digit = false;
                    humanName.append(" ");
                } else if (upper && !Character.isUpperCase(name[i + 1])) {
                    if (upCount == 2) {
                        humanName.insert(humanName.length() - 2, " ");
                    }

                    upper = false;
                    upCount = 0;

                    humanName.insert(humanName.length() - 1, " ");
                } else if (Character.isUpperCase(name[i + 1])) {
                    upCount++;
                    upper = true;
                }
            }
        }

        return humanName.toString().replaceAll("^ ", "");
    }
}

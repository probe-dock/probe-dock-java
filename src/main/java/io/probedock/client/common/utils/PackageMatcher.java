package io.probedock.client.common.utils;

import minimatch.Minimatch;

import java.util.Map;

/**
 * Match the package against kind of minimatch patterns.
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class PackageMatcher {
    /**
     * Check if there is a pattern in the map that match the given package
     *
     * @param categoriesByPackage The map of package patterns
     * @param pkg The package to check against
     * @return The entry with the corresponding match or null if no match
     */
    public static Map.Entry<String, String> match(Map<String, String> categoriesByPackage, String pkg) {
        if (categoriesByPackage != null && pkg != null) {
            for (Map.Entry<String, String> e : categoriesByPackage.entrySet()) {
                if (Minimatch.minimatch(pkg.replaceAll("\\.", "/"), e.getKey().replaceAll("\\.", "/"))) {
                    return e;
                }
            }
        }

        return null;
    }

}

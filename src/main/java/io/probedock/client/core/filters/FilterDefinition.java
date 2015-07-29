package io.probedock.client.core.filters;

/**
 * Filter definition
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface FilterDefinition {
    /**
     * @return The type of the filter
     */
    String getType();

    /**
     * @return The string to match in the filter processing
     */
    String getText();
}

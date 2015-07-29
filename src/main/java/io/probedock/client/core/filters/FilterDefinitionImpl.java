package io.probedock.client.core.filters;

/**
 * Define the structure of a filter. Basically, it consists of a type and a text
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class FilterDefinitionImpl implements FilterDefinition {
    private String type;
    private String text;

    public FilterDefinitionImpl(String type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return
            "type: " + type +
            ", text: " + text;
    }
}

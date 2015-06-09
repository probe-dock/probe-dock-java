package io.probedock.client.common.model;

/**
 * Context of test execution
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface ProbeContext {
    String OS_NAME = "os.name";
    String OS_VERSION = "os.version";
    String OS_ARCHITECTURE = "os.architecture";

    String JAVA_VERSION = "java.version";
    String JAVA_VENDOR = "java.vendor";

    String JAVA_RUNTIME_NAME = "java.runtime.name";
    String JAVA_RUNTIME_VERSION = "java.runtime.version";

    String JAVA_VM_NAME = "java.vm.name";
    String JAVA_VM_VERSION = "java.vm.version";
    String JAVA_VM_VENDOR = "java.vm.vendor";

    String JAVA_VM_SPEC_NAME = "java.vm.specification.name";
    String JAVA_VM_SPEC_VERSION = "java.vm.specification.version";
    String JAVA_VM_SPEC_VENDOR = "java.vm.specification.vendor";

    String JAVA_SPEC_NAME = "java.specification.name";
    String JAVA_SPEC_VERSION = "java.specification.version";
    String JAVA_SPEC_VENDOR = "java.specification.vendor";

    String JAVA_CLASS_VERSION = "java.class.version";

    String MEMORY_TOTAL = "memory.total";
    String MEMORY_USED = "memory.used";
    String MEMORY_FREE = "memory.free";

    /**
     * @param name The name of the property to retrieve
     * @return The property retrieved, null if not set
     */
    Object getProperty(String name);

    /**
     * @param name The name of the property
     * @param value The value to set to this property
     */
    void setProperty(String name, Object value);

    /**
     * @param name The name of the property to retrieve. The property
     *             name is scoped to the properties that are before the run.
     * @return The property retrieved, null if not
     */
    Object getPreProperty(String name);

    /**
     * @param name The name of the property
     * @param value The value to set to this property. The property is scoped to the
     *              properties defined before the run.
     */
    void setPreProperty(String name, Object value);

    /**
     * @param name The name of the property to retrieve. The property
     *             name is scoped to the properties that are after the run.
     * @return The property retrieved, null if not
     */
    Object getPostProperty(String name);

    /**
     * @param name The name of the property
     * @param value The value to set to this property. The property is scoped to the
     *              properties defined after the run.
     */
    void setPostProperty(String name, Object value);
}

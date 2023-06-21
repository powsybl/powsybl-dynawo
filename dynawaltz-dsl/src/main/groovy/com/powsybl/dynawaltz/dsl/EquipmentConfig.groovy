
package com.powsybl.dynawaltz.dsl

class EquipmentConfig {

    static final String CONTROLLABLE_PROPERTY = "CONTROLLABLE"
    static final String DANGLING_PROPERTY = "DANGLING"
    static final String SYNCHRONIZED_PROPERTY = "SYNCHRONIZED"

    final String lib
    final String prefix
    final List<String> properties

    EquipmentConfig(String lib, String prefix, String... properties) {
        this.lib = lib
        this.prefix = prefix
        this.properties = properties
    }

    boolean isControllable() {
        properties.contains(CONTROLLABLE_PROPERTY)
    }

    boolean isDangling() {
        properties.contains(DANGLING_PROPERTY)
    }

    boolean isSynchronized() {
        properties.contains(SYNCHRONIZED_PROPERTY)
    }

    boolean hasProperty(String property) {
        properties.contains(property)
    }
}

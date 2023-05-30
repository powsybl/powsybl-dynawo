
package com.powsybl.dynawaltz.dsl

class EquipmentConfig {

    static final String CONTROLLABLE_PROPERTY = "CONTROLLABLE"

    String lib
    List<String> properties

    EquipmentConfig(String lib) {
        this.lib = lib
        this.properties = []
    }

    EquipmentConfig(String lib, String... properties) {
        this.lib = lib
        this.properties = properties
    }

    boolean isControllable() {
        properties.contains(CONTROLLABLE_PROPERTY)
    }

    boolean hasProperty(String property) {
        properties.contains(property)
    }
}

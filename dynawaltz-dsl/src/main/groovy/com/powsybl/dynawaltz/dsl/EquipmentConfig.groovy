package com.powsybl.dynawaltz.dsl

class EquipmentConfig {

    String lib
    List<EnumEquipmentProperty> properties

    EquipmentConfig(String lib) {
        this.lib = lib
        this.properties = []
    }

    EquipmentConfig(String lib, List<EnumEquipmentProperty> properties) {
        this.lib = lib
        this.properties = properties
    }

    boolean isControllable() {
        return properties.contains(EnumEquipmentProperty.CONTROLLABLE)
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
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

    EquipmentConfig(String lib) {
        this.lib = lib
        this.prefix = ""
        this.properties = []
    }

    boolean isControllable() {
        properties.contains(CONTROLLABLE_PROPERTY)
    }

    boolean hasProperty(String property) {
        properties.contains(property)
    }
}

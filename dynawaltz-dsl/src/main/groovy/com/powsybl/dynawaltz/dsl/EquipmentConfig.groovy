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
    static final String DANGLING_PROPERTY = "DANGLING"
    static final String SYNCHRONIZED_PROPERTY = "SYNCHRONIZED"
    static final String TRANSFORMER_PROPERTY = "TRANSFORMER"
    static final String AUXILIARY_PROPERTY = "AUXILIARY"

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

    boolean hasTransformer() {
        properties.contains(TRANSFORMER_PROPERTY)
    }

    boolean hasAuxiliary() {
        properties.contains(AUXILIARY_PROPERTY)
    }

    boolean hasProperty(String property) {
        properties.contains(property)
    }
}

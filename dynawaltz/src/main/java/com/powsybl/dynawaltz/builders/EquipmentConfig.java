/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EquipmentConfig {
    //TODO a renommer on est plus sur une config dynamic model que d'equipment (peut être un automate)

    private static final String CONTROLLABLE_PROPERTY = "CONTROLLABLE";
    private static final String DANGLING_PROPERTY = "DANGLING";
    private static final String SYNCHRONIZED_PROPERTY = "SYNCHRONIZED";
    private static final String TRANSFORMER_PROPERTY = "TRANSFORMER";
    private static final String AUXILIARY_PROPERTY = "AUXILIARY";
    private final String lib;
    private final String internalModelPrefix;
    private final List<String> properties;

    public EquipmentConfig(String lib, String internalModelPrefix, String... properties) {
        this.lib = lib;
        this.internalModelPrefix = internalModelPrefix;
        this.properties = Arrays.stream(properties).map(String::toUpperCase).toList();
    }

    public EquipmentConfig(String lib, String internalModelPrefix) {
        this.lib = lib;
        this.internalModelPrefix = internalModelPrefix;
        this.properties = new ArrayList<>();
    }

    public EquipmentConfig(String lib) {
        this.lib = lib;
        this.internalModelPrefix = "";
        this.properties = new ArrayList<>();
    }

    public boolean isControllable() {
        return properties.contains(CONTROLLABLE_PROPERTY);
    }

    public boolean isDangling() {
        return properties.contains(DANGLING_PROPERTY);
    }

    public boolean isSynchronized() {
        return properties.contains(SYNCHRONIZED_PROPERTY);
    }

    public boolean hasTransformer() {
        return properties.contains(TRANSFORMER_PROPERTY);
    }

    public boolean hasAuxiliary() {
        return properties.contains(AUXILIARY_PROPERTY);
    }

    public boolean hasProperty(String property) {
        return properties.contains(property);
    }

    public String getLib() {
        return lib;
    }

    public String getInternalModelPrefix() {
        return internalModelPrefix;
    }
}

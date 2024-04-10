/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.suppliers.Property;
import com.powsybl.dynawaltz.suppliers.SetGroupType;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicModelConfig {

    private final String model;
    private final String group;
    private final List<Property> properties;

    public DynamicModelConfig(String model, String group, SetGroupType groupType, List<Property> properties) {
        this.model = model;
        this.group = switch (groupType) {
            case FIXED -> group;
            case PREFIX -> group + getDynamicModelIdProperty(properties);
            case SUFFIX -> getDynamicModelIdProperty(properties) + group;
        };
        this.properties = properties;
    }

    public DynamicModelConfig(String model, String group, List<Property> properties) {
        this.model = model;
        this.group = group;
        this.properties = properties;
    }

    public String getModel() {
        return model;
    }

    public String getGroup() {
        return group;
    }

    public List<Property> getProperties() {
        return properties;
    }

    private static String getDynamicModelIdProperty(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.name().equalsIgnoreCase("dynamicModelId"))
                .map(p -> (String) p.value())
                .findFirst()
                .orElseGet(() -> DynamicModelConfig.getStaticIdProperty(properties));
    }

    private static String getStaticIdProperty(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.name().equalsIgnoreCase("staticId"))
                .map(p -> (String) p.value())
                .findFirst()
                .orElseThrow(() -> new PowsyblException("No ID found for parameter set id"));
    }
}


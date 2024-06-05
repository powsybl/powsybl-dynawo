/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.dynamicmodels;

import com.powsybl.dynawaltz.suppliers.Property;
import com.powsybl.dynawaltz.suppliers.SetGroupType;

import java.util.List;

/**
 * Dynamic model configuration deserialized by {@link DynamicModelConfigsJsonDeserializer}
 * used to configure dynamic model builder in {@link DynawoModelsSupplier}
 * @param model alias of the library used for this model
 * @param group represents model parameter set id or part of it depending on {@link #groupType}
 * @param groupType configures {@link #group} processing
 * @param properties list of properties used by the builder
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicModelConfig(String model, String group, SetGroupType groupType, List<Property> properties) {

    public DynamicModelConfig(String model, String group, List<Property> properties) {
        this(model, group, SetGroupType.FIXED, properties);
    }
}


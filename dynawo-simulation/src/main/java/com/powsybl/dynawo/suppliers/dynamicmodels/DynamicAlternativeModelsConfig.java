/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.dynawo.suppliers.SetGroupType;

import java.util.List;

/**
 * Dynamic alternative models configuration deserialized by {@link DynamicModelConfigsJsonDeserializer}
 * used to configure dynamic model builder in {@link DynawoModelsSupplier}
 * @param alternativeModelConfigs list of alias of the library and parameter set ids
 * @param groupType configures group processing
 * @param modelResolverName anme of the {@link ModelResolver} used to select model and parameter set id in {@link #alternativeModelConfigs} list.
 * @param properties list of properties used by the builder
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicAlternativeModelsConfig(List<AlternativeModelConfig> alternativeModelConfigs, String modelResolverName,
                                             SetGroupType groupType, List<Property> properties) {
}


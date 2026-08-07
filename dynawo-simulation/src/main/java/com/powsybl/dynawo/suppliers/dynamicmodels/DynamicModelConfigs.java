/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import java.util.Collections;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicModelConfigs(List<DynamicModelConfig> dynamicModelConfigList,
                                  List<DynamicAlternativeModelsConfig> dynamicAlternativeModelsConfiglist) {

    public DynamicModelConfigs(List<DynamicModelConfig> dynamicModelConfigList) {
        this(dynamicModelConfigList, Collections.emptyList());
    }

    boolean hasDynamicAlternativeModels() {
        return !dynamicAlternativeModelsConfiglist.isEmpty();
    }
}

/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions;

import java.util.Collections;
import java.util.List;

/**
 * @param dynamicModelId The dynamic model id for the model used in the simulation
 * @param modelName The dynamic model name used in the simulation
 * @param operatedModelIds List dynamic model ids monitored by the automation system (can be others dynamic automation system or network equipment)
 * @param monitoredModelIds List dynamic model ids operated by the automation system (can be others dynamic automation system or network equipment)
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicAutomationSystemInfo(String dynamicModelId, String modelName, List<String> operatedModelIds,
                                          List<String> monitoredModelIds) {

    public DynamicAutomationSystemInfo(String dynamicModelId, String modelName, List<String> operatedModelIds) {
        this(dynamicModelId, modelName, operatedModelIds, Collections.emptyList());
    }

    public DynamicAutomationSystemInfo(String dynamicModelId, String modelName, String operatedModelId,
                                       String monitoredModelId) {
        this(dynamicModelId, modelName, List.of(operatedModelId), List.of(monitoredModelId));
    }

    public DynamicAutomationSystemInfo(String dynamicModelId, String modelName, String operatedModelId) {
        this(dynamicModelId, modelName, List.of(operatedModelId), Collections.emptyList());
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;

import java.util.List;
import java.util.Map;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record ContingencyEventModels(Contingency contingency, List<BlackBoxModel> eventModels,
                                     Map<String, MacroConnector> macroConnectorsMap,
                                     List<MacroConnect> macroConnectList, List<ParametersSet> eventParameters) {

    public String getId() {
        return contingency.getId();
    }
}

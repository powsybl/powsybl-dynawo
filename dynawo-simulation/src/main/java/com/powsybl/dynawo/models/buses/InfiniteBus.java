/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.iidm.network.Bus;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class InfiniteBus extends AbstractBus {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("infiniteBus_UPuVar", "v"),
            new VarMapping("infiniteBus_UPhaseVar", "angle"));

    protected InfiniteBus(String dynamicModelId, Bus bus, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, bus, parameterSetId, modelConfig);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public String getTerminalVarName() {
        return "infiniteBus_terminal";
    }
}

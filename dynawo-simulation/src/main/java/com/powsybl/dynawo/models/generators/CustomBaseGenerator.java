/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.iidm.network.Generator;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CustomBaseGenerator extends BaseGenerator implements SpecifiedGeneratorModel {

    private final CustomGeneratorComponent customComponent;

    protected CustomBaseGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
        this.customComponent = CustomGeneratorComponent.fromModelConfig(modelConfig);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return customComponent.varMapping();
    }

    @Override
    public String getTerminalVarName() {
        return customComponent.terminal();
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return customComponent.switchOffSignalNode();
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return customComponent.switchOffSignalEvent();
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return customComponent.switchOffSignalAutomaton();
    }
}

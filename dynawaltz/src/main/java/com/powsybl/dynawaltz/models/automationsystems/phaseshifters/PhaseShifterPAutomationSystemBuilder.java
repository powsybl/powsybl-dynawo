/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.phaseshifters;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterPAutomationSystemBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterPAutomationSystemBuilder> {

    private static final String CATEGORY = "phaseShiftersP";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static PhaseShifterPAutomationSystemBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static PhaseShifterPAutomationSystemBuilder of(Network network, Reporter reporter) {
        return new PhaseShifterPAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reporter);
    }

    public static PhaseShifterPAutomationSystemBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static PhaseShifterPAutomationSystemBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, PhaseShifterPAutomationSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new PhaseShifterPAutomationSystemBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected PhaseShifterPAutomationSystemBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public PhaseShifterPAutomationSystem build() {
        return isInstantiable() ? new PhaseShifterPAutomationSystem(dynamicModelId, transformer.getEquipment(), parameterSetId, getLib()) : null;
    }

    @Override
    protected PhaseShifterPAutomationSystemBuilder self() {
        return this;
    }
}

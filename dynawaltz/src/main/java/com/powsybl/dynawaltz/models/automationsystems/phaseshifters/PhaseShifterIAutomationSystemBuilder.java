/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.phaseshifters;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterIAutomationSystemBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterIAutomationSystemBuilder> {

    private static final String CATEGORY = "phaseShiftersI";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static PhaseShifterIAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static PhaseShifterIAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new PhaseShifterIAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static PhaseShifterIAutomationSystemBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static PhaseShifterIAutomationSystemBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, PhaseShifterIAutomationSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new PhaseShifterIAutomationSystemBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected PhaseShifterIAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public PhaseShifterIAutomationSystem build() {
        return isInstantiable() ? new PhaseShifterIAutomationSystem(dynamicModelId, transformer.getEquipment(), parameterSetId, getLib()) : null;
    }

    @Override
    protected PhaseShifterIAutomationSystemBuilder self() {
        return this;
    }
}

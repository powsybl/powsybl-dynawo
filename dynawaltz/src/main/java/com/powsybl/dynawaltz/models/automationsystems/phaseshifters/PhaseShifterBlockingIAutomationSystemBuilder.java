/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.phaseshifters;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.models.automationsystems.AbstractAutomationSystemModelBuilder;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterBlockingIAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<PhaseShifterBlockingIAutomationSystemBuilder> {

    public static final String CATEGORY = "PHASE_SHIFTER_BLOCKING_I";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static PhaseShifterBlockingIAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static PhaseShifterBlockingIAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new PhaseShifterBlockingIAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static PhaseShifterBlockingIAutomationSystemBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static PhaseShifterBlockingIAutomationSystemBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, PhaseShifterBlockingIAutomationSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new PhaseShifterBlockingIAutomationSystemBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    private String phaseShifterIDynamicId;

    protected PhaseShifterBlockingIAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    public PhaseShifterBlockingIAutomationSystemBuilder phaseShifterId(String phaseShifterIDynamicId) {
        this.phaseShifterIDynamicId = phaseShifterIDynamicId;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (phaseShifterIDynamicId == null) {
            BuilderReports.reportFieldNotSet(reportNode, "phaseShifterId");
            isInstantiable = false;
        }
    }

    @Override
    public PhaseShifterBlockingIAutomationSystem build() {
        return isInstantiable() ? new PhaseShifterBlockingIAutomationSystem(dynamicModelId, phaseShifterIDynamicId, parameterSetId, getLib()) : null;
    }

    @Override
    protected PhaseShifterBlockingIAutomationSystemBuilder self() {
        return this;
    }
}

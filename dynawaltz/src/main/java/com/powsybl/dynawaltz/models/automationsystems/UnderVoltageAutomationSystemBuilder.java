/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class UnderVoltageAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<UnderVoltageAutomationSystemBuilder> {

    private static final String CATEGORY = "underVoltages";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Generator> generator;

    public static UnderVoltageAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static UnderVoltageAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new UnderVoltageAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static UnderVoltageAutomationSystemBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static UnderVoltageAutomationSystemBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, UnderVoltageAutomationSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new UnderVoltageAutomationSystemBuilder(network, MODEL_CONFIGS.getModelConfig(lib), reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected UnderVoltageAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
        generator = new BuilderEquipment<>(IdentifiableType.GENERATOR, "generator");
    }

    public UnderVoltageAutomationSystemBuilder generator(String staticId) {
        generator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= generator.checkEquipmentData(reportNode);
    }

    @Override
    public UnderVoltageAutomationSystem build() {
        return isInstantiable() ? new UnderVoltageAutomationSystem(dynamicModelId, parameterSetId, generator.getEquipment(), getLib()) : null;
    }

    @Override
    protected UnderVoltageAutomationSystemBuilder self() {
        return this;
    }
}

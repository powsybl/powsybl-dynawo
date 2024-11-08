/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<TapChangerAutomationSystemBuilder> {

    public static final String CATEGORY = "TAP_CHANGER";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Load> load;
    protected TransformerSide side = TransformerSide.NONE;

    public static TapChangerAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new TapChangerAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, TapChangerAutomationSystemBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new TapChangerAutomationSystemBuilder(network, modelConfig, reportNode);
    }

    public static ModelInfo getDefaultModelInfo() {
        return MODEL_CONFIGS.getDefaultModelConfig();
    }

    public static Collection<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    /**
     * Returns models usable with the given {@link DynawoVersion}
     */
    public static Collection<ModelInfo> getSupportedModelInfos(DynawoVersion dynawoVersion) {
        return MODEL_CONFIGS.getModelInfos(dynawoVersion);
    }

    protected TapChangerAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
        load = new BuilderEquipment<>(IdentifiableType.LOAD);
    }

    public TapChangerAutomationSystemBuilder staticId(String staticId) {
        load.addEquipment(staticId, network::getLoad);
        return self();
    }

    public TapChangerAutomationSystemBuilder side(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= load.checkEquipmentData(reportNode);
    }

    @Override
    public TapChangerAutomationSystem build() {
        return isInstantiable() ? new TapChangerAutomationSystem(dynamicModelId, parameterSetId, load.getEquipment(), side, modelConfig) : null;
    }

    @Override
    protected TapChangerAutomationSystemBuilder self() {
        return this;
    }
}

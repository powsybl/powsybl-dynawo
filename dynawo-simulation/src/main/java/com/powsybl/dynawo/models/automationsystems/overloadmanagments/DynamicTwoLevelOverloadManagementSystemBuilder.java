/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.overloadmanagments;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicTwoLevelOverloadManagementSystemBuilder extends AbstractOverloadManagementSystemBuilder<DynamicTwoLevelOverloadManagementSystemBuilder> {

    public static final String CATEGORY = "TWO_LEVEL_OVERLOAD_MANAGEMENT";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Branch<?>> iMeasurement2;
    protected TwoSides iMeasurement2Side;

    public static DynamicTwoLevelOverloadManagementSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static DynamicTwoLevelOverloadManagementSystemBuilder of(Network network, ReportNode reportNode) {
        return new DynamicTwoLevelOverloadManagementSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static DynamicTwoLevelOverloadManagementSystemBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static DynamicTwoLevelOverloadManagementSystemBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, DynamicTwoLevelOverloadManagementSystemBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new DynamicTwoLevelOverloadManagementSystemBuilder(network, modelConfig, reportNode);
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

    protected DynamicTwoLevelOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode, "iMeasurement1", "controlledBranch1");
        iMeasurement2 = new BuilderEquipment<>(BRANCH_TYPE, "iMeasurement2", reportNode);
    }

    public DynamicTwoLevelOverloadManagementSystemBuilder iMeasurement1(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public DynamicTwoLevelOverloadManagementSystemBuilder iMeasurement1Side(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    public DynamicTwoLevelOverloadManagementSystemBuilder iMeasurement2(String staticId) {
        iMeasurement2.addEquipment(staticId, network::getBranch);
        return self();
    }

    public DynamicTwoLevelOverloadManagementSystemBuilder iMeasurement2Side(TwoSides side) {
        this.iMeasurement2Side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= iMeasurement2.checkEquipmentData();
        if (iMeasurement2Side == null) {
            BuilderReports.reportFieldNotSet(reportNode, "iMeasurement2Side");
            isInstantiable = false;
        }
    }

    @Override
    public DynamicTwoLevelOverloadManagementSystem build() {
        return isInstantiable() ? new DynamicTwoLevelOverloadManagementSystem(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, iMeasurement2.getEquipment(), iMeasurement2Side,
                controlledEquipment.getEquipment(), modelConfig)
                : null;
    }

    @Override
    protected DynamicTwoLevelOverloadManagementSystemBuilder self() {
        return this;
    }
}

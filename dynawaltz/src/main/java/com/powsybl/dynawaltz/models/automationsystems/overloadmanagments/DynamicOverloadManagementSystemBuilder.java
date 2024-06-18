/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.overloadmanagments;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicOverloadManagementSystemBuilder extends AbstractOverloadManagementSystemBuilder<DynamicOverloadManagementSystemBuilder> {

    public static final String CATEGORY = "OVERLOAD_MANAGEMENT";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static DynamicOverloadManagementSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, ReportNode reportNode) {
        return new DynamicOverloadManagementSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, DynamicOverloadManagementSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new DynamicOverloadManagementSystemBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected DynamicOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode, new BuilderEquipment<>(BRANCH_TYPE, "iMeasurement"),
            new BuilderEquipment<>(BRANCH_TYPE, "controlledBranch"));
    }

    public DynamicOverloadManagementSystemBuilder iMeasurement(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public DynamicOverloadManagementSystemBuilder iMeasurementSide(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    @Override
    public DynamicOverloadManagementSystem build() {
        return isInstantiable() ? new DynamicOverloadManagementSystem(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, controlledEquipment.getEquipment(), getLib())
                : null;
    }

    @Override
    protected DynamicOverloadManagementSystemBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.overloadmanagments;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicOverloadManagementSystemBuilder extends AbstractOverloadManagementSystemBuilder<DynamicOverloadManagementSystemBuilder> {

    private static final String CATEGORY = "overloadManagements";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static DynamicOverloadManagementSystemBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, Reporter reporter) {
        return new DynamicOverloadManagementSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reporter);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static DynamicOverloadManagementSystemBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, DynamicOverloadManagementSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new DynamicOverloadManagementSystemBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected DynamicOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter, new BuilderEquipment<>(BRANCH_TYPE, "iMeasurement"),
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

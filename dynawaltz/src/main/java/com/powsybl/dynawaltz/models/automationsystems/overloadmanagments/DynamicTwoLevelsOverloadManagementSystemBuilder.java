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
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicTwoLevelsOverloadManagementSystemBuilder extends AbstractOverloadManagementSystemBuilder<DynamicTwoLevelsOverloadManagementSystemBuilder> {

    private static final String CATEGORY = "twoLevelsOverloadManagements";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Branch<?>> iMeasurement2;
    protected TwoSides iMeasurement2Side;

    public static DynamicTwoLevelsOverloadManagementSystemBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static DynamicTwoLevelsOverloadManagementSystemBuilder of(Network network, Reporter reporter) {
        return new DynamicTwoLevelsOverloadManagementSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reporter);
    }

    public static DynamicTwoLevelsOverloadManagementSystemBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static DynamicTwoLevelsOverloadManagementSystemBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, DynamicTwoLevelsOverloadManagementSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new DynamicTwoLevelsOverloadManagementSystemBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected DynamicTwoLevelsOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter, new BuilderEquipment<>(BRANCH_TYPE, "iMeasurement1"),
                new BuilderEquipment<>(BRANCH_TYPE, "controlledBranch1"));
        iMeasurement2 = new BuilderEquipment<>(BRANCH_TYPE, "iMeasurement2");
    }

    public DynamicTwoLevelsOverloadManagementSystemBuilder iMeasurement1(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public DynamicTwoLevelsOverloadManagementSystemBuilder iMeasurement1Side(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    public DynamicTwoLevelsOverloadManagementSystemBuilder iMeasurement2(String staticId) {
        iMeasurement2.addEquipment(staticId, network::getBranch);
        return self();
    }

    public DynamicTwoLevelsOverloadManagementSystemBuilder iMeasurement2Side(TwoSides side) {
        this.iMeasurement2Side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= iMeasurement2.checkEquipmentData(reporter);
        if (iMeasurement2Side == null) {
            Reporters.reportFieldNotSet(reporter, "iMeasurement2Side");
            isInstantiable = false;
        }
    }

    @Override
    public DynamicTwoLevelsOverloadManagementSystem build() {
        return isInstantiable() ? new DynamicTwoLevelsOverloadManagementSystem(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, iMeasurement2.getEquipment(), iMeasurement2Side,
                controlledEquipment.getEquipment(), getLib())
                : null;
    }

    @Override
    protected DynamicTwoLevelsOverloadManagementSystemBuilder self() {
        return this;
    }
}
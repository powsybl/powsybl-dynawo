/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.currentlimits;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurrentLimitTwoLevelsAutomatonBuilder extends AbstractCurrentLimitAutomatonBuilder<CurrentLimitTwoLevelsAutomatonBuilder> {

    public static final String CATEGORY = "CLA_TWO_LEVElS";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Branch<?>> iMeasurement2;
    protected TwoSides iMeasurement2Side;

    public static CurrentLimitTwoLevelsAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static CurrentLimitTwoLevelsAutomatonBuilder of(Network network, Reporter reporter) {
        return new CurrentLimitTwoLevelsAutomatonBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static CurrentLimitTwoLevelsAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static CurrentLimitTwoLevelsAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, CurrentLimitTwoLevelsAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new CurrentLimitTwoLevelsAutomatonBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected CurrentLimitTwoLevelsAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter, new BuilderEquipment<>(QUADRIPOLE_TYPE, "iMeasurement1"),
                new BuilderEquipment<>(QUADRIPOLE_TYPE, "controlledQuadripole1"));
        iMeasurement2 = new BuilderEquipment<>(QUADRIPOLE_TYPE, "iMeasurement2");
    }

    public CurrentLimitTwoLevelsAutomatonBuilder iMeasurement1(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitTwoLevelsAutomatonBuilder iMeasurement1Side(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    public CurrentLimitTwoLevelsAutomatonBuilder iMeasurement2(String staticId) {
        iMeasurement2.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitTwoLevelsAutomatonBuilder iMeasurement2Side(TwoSides side) {
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
    public CurrentLimitTwoLevelsAutomaton build() {
        return isInstantiable() ? new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, iMeasurement2.getEquipment(), iMeasurement2Side,
                controlledEquipment.getEquipment(), getLib())
                : null;
    }

    @Override
    protected CurrentLimitTwoLevelsAutomatonBuilder self() {
        return this;
    }
}

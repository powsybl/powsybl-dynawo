/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.currentLimits;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurrentLimitAutomatonBuilder extends AbstractCurrentLimitAutomatonBuilder<CurrentLimitAutomatonBuilder> {

    private static final String CATEGORY = "clas";
    private static final Map<String, ModelConfig> LIBS = ModelConfigsSingleton.getInstance().getModelConfigs(CATEGORY);

    public static CurrentLimitAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static CurrentLimitAutomatonBuilder of(Network network, Reporter reporter) {
        return new CurrentLimitAutomatonBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static CurrentLimitAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static CurrentLimitAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, CurrentLimitAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new CurrentLimitAutomatonBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected CurrentLimitAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter, new BuilderEquipment<>("Quadripole", "iMeasurement"),
            new BuilderEquipment<>("Quadripole", "controlledQuadripole"));
    }

    public CurrentLimitAutomatonBuilder iMeasurement(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitAutomatonBuilder iMeasurementSide(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    @Override
    public CurrentLimitAutomaton build() {
        return isInstantiable() ? new CurrentLimitAutomaton(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, controlledEquipment.getEquipment(), getLib())
                : null;
    }

    @Override
    protected CurrentLimitAutomatonBuilder self() {
        return this;
    }
}

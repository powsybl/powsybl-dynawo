/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurrentLimitAutomatonBuilder extends AbstractCurrentLimitAutomatonBuilder<CurrentLimitAutomatonBuilder> {

    public CurrentLimitAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter, new DslEquipment<>("Quadripole", "iMeasurement"),
            new DslEquipment<>("Quadripole", "controlledQuadripole"));
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

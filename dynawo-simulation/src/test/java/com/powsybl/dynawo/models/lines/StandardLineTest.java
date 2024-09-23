/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.lines;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.iidm.network.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
class StandardLineTest {

    @Test
    void connectionToDynamicOverloadManagementSystemException() {
        Network network = Network.create("test", "test");
        Substation s = network.newSubstation().setId("s").add();
        VoltageLevel vl1 = s.newVoltageLevel().setId("vl1").setNominalV(400).setTopologyKind(TopologyKind.BUS_BREAKER).add();
        VoltageLevel vl2 = s.newVoltageLevel().setId("vl2").setNominalV(400).setTopologyKind(TopologyKind.BUS_BREAKER).add();
        Bus b1 = vl1.getBusBreakerView().newBus().setId("bus1").add();
        Bus b2 = vl2.getBusBreakerView().newBus().setId("bus2").add();
        Line l = network.newLine().setId("l").setVoltageLevel1(vl1.getId()).setBus1(b1.getId()).setVoltageLevel2(vl2.getId()).setBus2(b2.getId())
                .setR(1).setX(3).setG1(0).setG2(0).setB1(0).setB2(0).add();

        List<BlackBoxModel> dynamicModels = new ArrayList<>();
        dynamicModels.add(new StandardLine("BBM_l", l, "SL", "Line"));
        dynamicModels.add(DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                        .dynamicModelId("BBM_CLA")
                        .parameterSetId("CLA")
                        .controlledBranch(l.getId())
                        .iMeasurement(l.getId())
                        .iMeasurementSide(TwoSides.ONE)
                        .build());
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        List<BlackBoxModel> events = Collections.emptyList();
        List<OutputVariable> outputVariables = Collections.emptyList();
        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
            () -> new DynawoSimulationContext(network, workingVariantId, dynamicModels, events, outputVariables, parameters, dynawoParameters));
        assertEquals("i variable not implemented in StandardLine dynawo's model", e.getMessage());
    }
}

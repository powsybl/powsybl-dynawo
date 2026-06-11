/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.frequencysynchronizers.PowerAngleModel;
import com.powsybl.dynawo.models.generators.SynchronousGenerator;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MacroConnectionsAdderTest {

    @Test
    public void testCreateMacroConnectionsForAll() {
        Network network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        SynchronousGenerator gen = SynchronousGeneratorBuilder.of(network)
                .staticId("GEN")
                .build();
        List<BlackBoxModel> dynamicModels = List.of(
                SynchronousGeneratorBuilder.of(network)
                    .staticId("GEN2")
                    .build(),
                SynchronousGeneratorBuilder.of(network)
                    .staticId("GEN3")
                    .build()
        );
        MacroConnectionsAdder adder = new MacroConnectionsAdder(
                BlackBoxModelSupplier.createFrom(dynamicModels),
                mc -> { },
                (mc, f) -> { },
                ReportNode.NO_OP);
        int createdMC = adder.createMacroConnectionsForAll(gen, PowerAngleModel.class, gen.getEquipment(), m -> List.of());
        assertEquals(2, createdMC);
    }
}

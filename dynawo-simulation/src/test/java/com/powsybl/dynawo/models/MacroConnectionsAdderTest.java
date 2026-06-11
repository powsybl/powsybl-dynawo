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
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MacroConnectionsAdderTest {

    private SynchronousGenerator gen;
    private List<BlackBoxModel> dynamicModels;

    @BeforeEach
    public void setUp() {
        Network network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        gen = SynchronousGeneratorBuilder.of(network)
                .staticId("GEN")
                .build();
        SynchronousGenerator gen2 = SynchronousGeneratorBuilder.of(network)
                .staticId("GEN2")
                .build();
        dynamicModels = List.of(
                gen,
                gen2,
                SynchronousGeneratorBuilder.of(network)
                        .staticId("GEN3")
                        .build()
        );
    }

    @Test
    public void testCreateMacroConnectionsOrSkip() {
        List<MacroConnector> macroConnectors = new ArrayList<>();
        MacroConnectionsAdder adder = new MacroConnectionsAdder(
                BlackBoxModelSupplier.createFrom(dynamicModels),
                mc -> { },
                (mc, f) -> macroConnectors.add(f.apply(mc)),
                ReportNode.NO_OP);
        boolean skipped = adder.createMacroConnectionsOrSkip(gen, gen.getEquipment(), PowerAngleModel.class, m -> List.of(), "Test");
        assertFalse(skipped);
        assertEquals(1, macroConnectors.size());
        assertThat(macroConnectors).hasSize(1);
        assertThat(macroConnectors.getFirst()).usingRecursiveComparison().isEqualTo(new MacroConnector("MC_GeneratorSynchronousFourWindingsTest-GeneratorSynchronousFourWindings", List.of()));
    }

    @Test
    public void testCreateMacroConnectionsForAll() {
        MacroConnectionsAdder adder = new MacroConnectionsAdder(
                BlackBoxModelSupplier.createFrom(dynamicModels),
                mc -> { },
                (mc, f) -> { },
                ReportNode.NO_OP);
        int createdMC = adder.createMacroConnectionsForAll(gen, PowerAngleModel.class, gen.getEquipment(), m -> List.of());
        assertEquals(2, createdMC);
    }
}

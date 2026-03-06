/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.InjectionModel;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystem;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.lines.LineModel;
import com.powsybl.dynawo.models.loads.BaseLoad;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelContextTest {

    protected Network network = EurostagTutorialExample1Factory.createWithLFResults();
    protected List<BlackBoxModel> dynamicModels;

    @BeforeEach
    void setup() {
        dynamicModels = new ArrayList<>();
    }

    @Test
    void duplicateStaticId() {
        BaseLoad load1 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        BaseLoad load2 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build();
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void duplicateDynamicId() {
        String duplicatedId = "LOAD";
        BaseLoad load = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId(duplicatedId)
                .parameterSetId("lab")
                .build();
        BlackBoxModel phaseShifter = TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId(duplicatedId)
                .parameterSetId("tc")
                .staticId("LOAD")
                .side(TransformerSide.LOW_VOLTAGE)
                .build();
        dynamicModels.add(load);
        dynamicModels.add(phaseShifter);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load);
    }

    @Test
    void wrongDynawoVersionModel() {
        dynamicModels.add(BaseLoadBuilder.of(network, "ElectronicLoad")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build());
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .currentVersion(new DynawoVersion(1, 2, 0))
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).isEmpty();
    }

    @Test
    void testIncorrectModelRequests() {
        Line line = network.getLine("NHV1_NHV2_1");
        BlackBoxModel bbm = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        dynamicModels.add(bbm);
        Generator gen = network.getGenerator("GEN");
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .equipment(gen)
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("TAP_CHANGER_1")
                .parameterSetId("tc")
                .staticId("LOAD")
                .side(TransformerSide.LOW_VOLTAGE)
                .build());
        MacroConnectionsAdder adder = createBasicMacroConnectionsAdder();



        // default model not found exception
        Exception e = assertThrows(PowsyblException.class, () ->
                adder.createMacroConnections(bbm, line, InjectionModel.class, l -> List.of()));
        assertEquals("LoadAlphaBeta LOAD requires a connection with a InjectionModel but dynamic model DefaultLine NHV1_NHV2_1 does not implement it", e.getMessage());
        // default model not found log
        assertTrue(adder.createMacroConnectionsOrSkip(bbm, line, InjectionModel.class, l -> List.of()));
        // implementation exception
        e = assertThrows(PowsyblException.class, () -> adder.createMacroConnections(bbm, gen, LineModel.class, l -> List.of()));
        assertEquals("LoadAlphaBeta LOAD requires a connection with a LineModel but dynamic model GeneratorFictitious GEN does not implement it", e.getMessage());
        // implementation log
        assertTrue(adder.createMacroConnectionsOrSkip(bbm, gen, LineModel.class, l -> List.of()));
        // pure dynamic implementation log
        assertTrue(adder.createMacroConnectionsOrSkip(bbm, "TAP_CHANGER_1", PhaseShifterIAutomationSystem.class, l -> List.of()));
        // pure dynamic model not found
        assertTrue(adder.createMacroConnectionsOrSkip(bbm, "TAP_CHANGER_2", TapChangerAutomationSystem.class, l -> List.of()));
    }

    private MacroConnectionsAdder createBasicMacroConnectionsAdder() {
        Map<String, BlackBoxModel> map = dynamicModels.stream().collect(Collectors.toMap(
                BlackBoxModel::getDynamicModelId,
                Function.identity()));
        return new MacroConnectionsAdder(map::get, map::get, mc -> { },
                (mc, f) -> { }, ReportNode.NO_OP);
    }
}

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
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.lines.LineModel;
import com.powsybl.dynawo.models.loads.BaseLoad;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        BlackBoxModel phaseShifter = PhaseShifterPAutomationSystemBuilder.of(network)
                .dynamicModelId(duplicatedId)
                .transformer("NGEN_NHV1")
                .parameterSetId("PS")
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
    void testIncorrectModelException() {

        BlackBoxModel bbm = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        Generator gen = network.getGenerator("GEN");
        BlackBoxModel bbm_gen = BaseGeneratorBuilder.of(network)
                .equipment(gen)
                .build();
        dynamicModels.add(bbm);
        dynamicModels.add(bbm_gen);
        MacroConnectionsAdder adder = createBasicMacroConnectionsAdder();

        // default model not found

        // implementation exception
        Exception e = assertThrows(PowsyblException.class, () -> adder.createMacroConnections(bbm, gen, LineModel.class, l -> List.of()));
        assertEquals("LoadAlphaBeta LOAD require a connection with a LineModel but GENERATOR GEN does not implement it", e.getMessage());
        // pure dyna impl log
        adder.createMacroConnectionsOrSkip(bbm, "TAP_CHANGER_1", PhaseShifterIAutomationSystem.class, l -> List.of());
        // pure dynamic model not found
        adder.createMacroConnectionsOrSkip(bbm, "TAP_CHANGER_2", TapChangerAutomationSystem.class, l -> List.of());
    }

    private MacroConnectionsAdder createBasicMacroConnectionsAdder() {
        Map<String, BlackBoxModel> map = dynamicModels.stream().collect(Collectors.toMap(
                BlackBoxModel::getDynamicModelId,
                Function.identity()));
        return new MacroConnectionsAdder(map::get, map::get, mc -> { },
                (mc, f) -> { }, ReportNode.NO_OP);
    }
}

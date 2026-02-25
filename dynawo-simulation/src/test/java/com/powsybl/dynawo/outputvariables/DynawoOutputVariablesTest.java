/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class DynawoOutputVariablesTest {

    protected Network network;
    protected List<BlackBoxModel> dynamicModels;
    protected List<OutputVariable> outputVariables;

    @BeforeEach
    void setup() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        outputVariables = new ArrayList<>();
        dynamicModels = new ArrayList<>();

        dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .staticId("LOAD")
                .parameterSetId("lot")
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_LOAD")
                .parameterSetId("tc")
                .staticId("LOAD")
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_LOAD2")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .build());
    }

    @Test
    void testResolvedOutputVariables() {
        // connected equipment dynamic model -> kept
        new DynawoOutputVariablesBuilder()
                .id("LOAD")
                .variables("load_PPu")
                .outputType(OutputVariable.OutputType.CURVE)
                .add(outputVariables::add);
        // connected default model -> set to default and kept
        new DynawoOutputVariablesBuilder()
                .id("GEN")
                .variables("Upu_value")
                .outputType(OutputVariable.OutputType.CURVE)
                .add(outputVariables::add);
        // connected automation system -> kept
        new DynawoOutputVariablesBuilder()
                .id("BBM_TC_LOAD")
                .variables("tapPosition")
                .outputType(OutputVariable.OutputType.CURVE)
                .add(outputVariables::add);
        // not connected automation system -> removed
        new DynawoOutputVariablesBuilder()
                .id("BBM_TC_LOAD2")
                .variables("tapPosition")
                .outputType(OutputVariable.OutputType.FINAL_STATE)
                .add(outputVariables::add);

        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();

        List<OutputVariable> result = context.getOutputVariables(OutputVariable.OutputType.CURVE);

        assertThat(result)
                .hasSize(3)
                .extracting(OutputVariable::getModelId, OutputVariable::getVariableName)
                    .containsExactly(
                            Tuple.tuple("LOAD", "load_PPu"),
                            Tuple.tuple("NETWORK", "GEN_Upu_value"),
                            Tuple.tuple("BBM_TC_LOAD", "tapPosition"));
        assertFalse(context.withFsvVariables());
    }
}

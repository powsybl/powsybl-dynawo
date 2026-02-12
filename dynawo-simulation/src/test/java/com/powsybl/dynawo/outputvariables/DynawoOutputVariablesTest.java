/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class DynawoOutputVariablesTest extends DynawoTestUtil {

    @BeforeEach
    void setup() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        outputVariables = new ArrayList<>();
        dynamicModels = new ArrayList<>();

        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_LOAD2")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .side("HIGH_VOLTAGE")
                .build());
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                .equipment(network.getGenerator("GEN2"))
                .parameterSetId("GSFWPR")
                .build());
    }

    @Test
    void resolveShouldRejectOutputVariableWhenTcbIsNotConnected() {
        outputVariables.addAll(new DynawoOutputVariablesBuilder()
                .id("BBM_TC_LOAD2")
                .variables("tapPosition")
                .outputType(OutputVariable.OutputType.FINAL_STATE)
                .build());
        outputVariables.addAll(new DynawoOutputVariablesBuilder()
                .id("GEN2")
                .variables("generator_omegaPu")
                .outputType(OutputVariable.OutputType.CURVE)
                .build());
        outputVariables.addAll(new DynawoOutputVariablesBuilder()
                .id("NLOAD")
                .variables("Upu_value")
                .outputType(OutputVariable.OutputType.CURVE)
                .build());

        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();

        assertEquals(2, context.getOutputVariables(OutputVariable.OutputType.CURVE).size());
        assertNull(context.getOutputVariables(OutputVariable.OutputType.FINAL_STATE));
    }
}

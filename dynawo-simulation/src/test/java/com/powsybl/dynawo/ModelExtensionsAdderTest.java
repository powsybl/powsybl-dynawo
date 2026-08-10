/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.*;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.dynawo.models.loads.LoadTwoTransformersBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelExtensionsAdderTest {

    @Test
    void testGeneratorExtensionAdderAndUpdate() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();

        List<BlackBoxModel> dynamicModels = List.of(
                SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                        .staticId("GEN")
                        .build(),
                UnderVoltageAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_UVA")
                        .parameterSetId("uv")
                        .generator("GEN")
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        Generator generator = network.getGenerator("GEN");
        DynawoEquipmentModel<Generator> modelInfo = generator.getExtension(DynawoEquipmentModel.class);
        assertEquals("GeneratorPQ", modelInfo.getModelName());
        DynawoUnderVoltageModel underVoltageModel = generator.getExtension(DynawoUnderVoltageModel.class);
        assertEquals("BBM_UVA", underVoltageModel.getDynamicModelId());

        //Update models

        dynamicModels = List.of(
                SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindings")
                        .staticId("GEN")
                        .build(),
                UnderVoltageAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_UVA2")
                        .parameterSetId("uv")
                        .generator("GEN")
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        assertEquals("GeneratorSynchronousFourWindings", modelInfo.getModelName());
        assertEquals("BBM_UVA2", underVoltageModel.getDynamicModelId());
    }

    @Test
    void testLoadExtensionAdderAndUpdate() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();

        List<BlackBoxModel> dynamicModels = List.of(
                LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                        .staticId("LOAD")
                        .parameterSetId("lot")
                        .build(),
                TapChangerAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_TC_LOAD")
                        .parameterSetId("tc")
                        .staticId("LOAD")
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        Load load = network.getLoad("LOAD");
        DynawoEquipmentModel<Load> loadModelInfo = load.getExtension(DynawoEquipmentModel.class);
        assertEquals("LoadOneTransformer", loadModelInfo.getModelName());
        DynawoTapChangerModel tapChangerModel = load.getExtension(DynawoTapChangerModel.class);
        assertEquals("BBM_TC_LOAD", tapChangerModel.getDynamicModelId());

        // Update models
        dynamicModels = List.of(
                LoadTwoTransformersBuilder.of(network, "LoadTwoTransformers")
                        .staticId("LOAD")
                        .parameterSetId("ltt")
                        .build(),
                TapChangerAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_TC_LOAD2")
                        .parameterSetId("tc")
                        .staticId("LOAD")
                        .side(TransformerSide.HIGH_VOLTAGE)
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        assertEquals("LoadTwoTransformers", loadModelInfo.getModelName());
        assertEquals("BBM_TC_LOAD2", tapChangerModel.getDynamicModelId());
    }

    @Test
    void testTfoExtensionAdderAndUpdate() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        network.getTwoWindingsTransformer(EurostagTutorialExample1Factory.NGEN_NHV1).newPhaseTapChanger()
                .setTapPosition(0)
                .beginStep().setR(1.0).setX(2.0).setG(3.0).setB(4.0).setAlpha(5.0).setRho(6.0).endStep()
                .add();

        List<BlackBoxModel> dynamicModels = List.of(
                PhaseShifterPAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSP")
                        .parameterSetId("psp")
                        .transformer("NGEN_NHV1")
                        .build(),
                PhaseShifterIAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSI")
                        .parameterSetId("ps")
                        .transformer("NGEN_NHV1")
                        .build(),
                PhaseShifterBlockingIAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSB")
                        .parameterSetId("psb")
                        .phaseShifterId("BBM_PSI")
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        TwoWindingsTransformer transformer = network.getTwoWindingsTransformer("NGEN_NHV1");
        DynawoPhaseShifterIModel phaseShifterIModel = transformer.getExtension(DynawoPhaseShifterIModel.class);
        assertEquals("BBM_PSI", phaseShifterIModel.getDynamicModelId());
        DynawoPhaseShifterPModel phaseShifterPModel = transformer.getExtension(DynawoPhaseShifterPModel.class);
        assertEquals("BBM_PSP", phaseShifterPModel.getDynamicModelId());
        DynawoPhaseShifterBlockingIModel phaseShifterBlockingIModel = transformer.getExtension(DynawoPhaseShifterBlockingIModel.class);
        assertEquals("BBM_PSB", phaseShifterBlockingIModel.getDynamicModelId());
        assertEquals("BBM_PSI", phaseShifterBlockingIModel.getPhaseShifterId());

        // Update models
        dynamicModels = List.of(
                PhaseShifterPAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSP2")
                        .parameterSetId("psp")
                        .transformer("NGEN_NHV1")
                        .build(),
                PhaseShifterIAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSI2")
                        .parameterSetId("ps")
                        .transformer("NGEN_NHV1")
                        .build(),
                PhaseShifterBlockingIAutomationSystemBuilder.of(network)
                        .dynamicModelId("BBM_PSB2")
                        .parameterSetId("psb")
                        .phaseShifterId("BBM_PSI2")
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        assertEquals("BBM_PSP2", phaseShifterPModel.getDynamicModelId());
        assertEquals("BBM_PSI2", phaseShifterBlockingIModel.getPhaseShifterId());
        assertEquals("BBM_PSB2", phaseShifterBlockingIModel.getDynamicModelId());
    }

    @Test
    void testLineExtensionAdderAndUpdate() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();

        List<BlackBoxModel> dynamicModels = List.of(
                DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                        .dynamicModelId("BBM_CLA")
                        .parameterSetId("cla")
                        .controlledBranch("NHV1_NHV2_1")
                        .iMeasurement("NHV2_NLOAD")
                        .iMeasurementSide(TwoSides.ONE)
                        .build(),
                DynamicTwoLevelOverloadManagementSystemBuilder.of(network, "TwoLevelOverloadManagementSystem")
                        .dynamicModelId("BBM_CLA_TWO_LEVEL")
                        .parameterSetId("cla")
                        .controlledBranch("NHV1_NHV2_1")
                        .iMeasurement1("NHV1_NHV2_1")
                        .iMeasurement1Side(TwoSides.TWO)
                        .iMeasurement2("NHV1_NHV2_2")
                        .iMeasurement2Side(TwoSides.ONE)
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        Line line = network.getLine("NHV1_NHV2_1");
        DynawoOverloadManagementSystemModel<Line> overloadManagementSystemModel = line.getExtension(DynawoOverloadManagementSystemModel.class);
        assertEquals("BBM_CLA", overloadManagementSystemModel.getDynamicModelId());
        DynawoTwoLevelOverloadManagementSystemModel<Line> twoLevelOverloadManagementSystemModel = line.getExtension(DynawoTwoLevelOverloadManagementSystemModel.class);
        assertEquals("BBM_CLA_TWO_LEVEL", twoLevelOverloadManagementSystemModel.getDynamicModelId());

        // Update models
        dynamicModels = List.of(
                DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                        .dynamicModelId("BBM_CLA2")
                        .parameterSetId("cla")
                        .controlledBranch("NHV1_NHV2_1")
                        .iMeasurement("NHV2_NLOAD")
                        .iMeasurementSide(TwoSides.ONE)
                        .build(),
                DynamicTwoLevelOverloadManagementSystemBuilder.of(network, "TwoLevelOverloadManagementSystem")
                        .dynamicModelId("BBM_CLA_TWO_LEVEL2")
                        .parameterSetId("cla")
                        .controlledBranch("NHV1_NHV2_1")
                        .iMeasurement1("NHV1_NHV2_1")
                        .iMeasurement1Side(TwoSides.TWO)
                        .iMeasurement2("NHV1_NHV2_2")
                        .iMeasurement2Side(TwoSides.ONE)
                        .build()
        );
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();

        assertEquals("BBM_CLA2", overloadManagementSystemModel.getDynamicModelId());
        assertEquals("BBM_CLA_TWO_LEVEL2", twoLevelOverloadManagementSystemModel.getDynamicModelId());
    }
}

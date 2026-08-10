/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.networkextension;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.*;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NetworkExtensionSupplierTest {

    @Test
    void testSupplierWithGeneratorExtensions() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        Generator generator = network.getGenerator("GEN");

        generator.newExtension(DynawoEquipmentModelAdder.class)
                .withModelName("GeneratorPV")
                .withParameterSetId("GPV")
                .add();
        generator.newExtension(DynawoUnderVoltageModelAdder.class)
                .withModelName("UnderVoltage")
                .withDynamicModelId("UVA")
                .withParameterSetId("UVA")
                .add();

        DynamicModel expectedGen = SynchronizedGeneratorBuilder.of(network, "GeneratorPV")
                .staticId("GEN")
                .parameterSetId("GPV")
                .build();
        DynamicModel expectedUva = UnderVoltageAutomationSystemBuilder.of(network, "UnderVoltage")
                .dynamicModelId("UVA")
                .parameterSetId("UVA")
                .generator("GEN")
                .build();

        List<DynamicModel> dynamicModels = new NetworkExtensionSupplier().get(network);
        assertThat(dynamicModels).hasSize(2).satisfiesExactlyInAnyOrder(
                gen -> assertThat(gen).usingRecursiveComparison().isEqualTo(expectedGen),
                uva -> assertThat(uva).usingRecursiveComparison().isEqualTo(expectedUva));
    }

    @Test
    void testSupplierWithLoadExtensions() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        Load load = network.getLoad("LOAD");

        load.newExtension(DynawoEquipmentModelAdder.class)
                .withModelName("LoadOneTransformer")
                .withParameterSetId("LOT")
                .add();

        load.newExtension(DynawoTapChangerModelAdder.class)
                .withModelName("TapChangerAutomationSystem")
                .withDynamicModelId("TC")
                .withParameterSetId("TC")
                .withSide(TransformerSide.NONE)
                .add();

        DynamicModel expectedLoad = LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .staticId("LOAD")
                .parameterSetId("LOT")
                .build();
        DynamicModel expectedTc = TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("TC")
                .parameterSetId("TC")
                .staticId("LOAD")
                .build();

        List<DynamicModel> dynamicModels = new NetworkExtensionSupplier().get(network);
        assertThat(dynamicModels).hasSize(2).satisfiesExactlyInAnyOrder(
                l -> assertThat(l).usingRecursiveComparison().isEqualTo(expectedLoad),
                tc -> assertThat(tc).usingRecursiveComparison().isEqualTo(expectedTc));
    }

    @Test
    void testSupplierWithPhaseShifterExtensions() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        network.getTwoWindingsTransformer(EurostagTutorialExample1Factory.NGEN_NHV1).newPhaseTapChanger()
                .setTapPosition(0)
                .beginStep().setR(1.0).setX(2.0).setG(3.0).setB(4.0).setAlpha(5.0).setRho(6.0).endStep()
                .add();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer(EurostagTutorialExample1Factory.NGEN_NHV1);

        tfo.newExtension(DynawoPhaseShifterPModelAdder.class)
                .withDynamicModelId("PSP")
                .withParameterSetId("PS")
                .withModelName("PhaseShifterP")
                .add();

        tfo.newExtension(DynawoPhaseShifterIModelAdder.class)
                .withDynamicModelId("PSI")
                .withParameterSetId("PS")
                .withModelName("PhaseShifterI")
                .add();

        tfo.newExtension(DynawoPhaseShifterBlockingIModelAdder.class)
                .withDynamicModelId("PSB")
                .withParameterSetId("PSB")
                .withModelName("PhaseShifterBlockingI")
                .withPhaseShifterId("PSI")
                .add();

        DynamicModel expectedPsp = PhaseShifterPAutomationSystemBuilder.of(network)
                .dynamicModelId("PSP")
                .parameterSetId("PS")
                .transformer("NGEN_NHV1")
                .build();
        DynamicModel expectedPsi = PhaseShifterIAutomationSystemBuilder.of(network)
                .dynamicModelId("PSI")
                .parameterSetId("PS")
                .transformer("NGEN_NHV1")
                .build();
        DynamicModel expectedPsb = PhaseShifterBlockingIAutomationSystemBuilder.of(network)
                .dynamicModelId("PSB")
                .parameterSetId("PSB")
                .phaseShifterId("PSI")
                .build();

        List<DynamicModel> dynamicModels = new NetworkExtensionSupplier().get(network);
        assertThat(dynamicModels).hasSize(3).satisfiesExactlyInAnyOrder(
                psi -> assertThat(psi).usingRecursiveComparison().isEqualTo(expectedPsi),
                psp -> assertThat(psp).usingRecursiveComparison().isEqualTo(expectedPsp),
                psb -> assertThat(psb).usingRecursiveComparison().isEqualTo(expectedPsb));
    }

    @Test
    void testSupplierWithOverloadManagementExtensions() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        Line line = network.getLine("NHV1_NHV2_1");

        DynawoOverloadManagementSystemModelAdder<Line> adder = line.newExtension(DynawoOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("BBM_CLA")
                .withParameterSetId("cla")
                .withModelName("OverloadManagementSystem")
                .withIMeasurement("NHV2_NLOAD")
                .withIMeasurementSide(TwoSides.ONE)
                .add();

        DynawoTwoLevelOverloadManagementSystemModelAdder<Line> adderTL = line.newExtension(DynawoTwoLevelOverloadManagementSystemModelAdder.class);
        adderTL.withDynamicModelId("BBM_CLA_TWO_LEVELS")
                .withParameterSetId("cla")
                .withModelName("TwoLevelOverloadManagementSystem")
                .withIMeasurement1("NHV2_NLOAD")
                .withIMeasurement1Side(TwoSides.ONE)
                .withIMeasurement2("NGEN_NHV1")
                .withIMeasurement2Side(TwoSides.TWO)
                .add();

        DynamicModel expectedOms = DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                .dynamicModelId("BBM_CLA")
                .parameterSetId("cla")
                .controlledBranch("NHV1_NHV2_1")
                .iMeasurement("NHV2_NLOAD")
                .iMeasurementSide(TwoSides.ONE)
                .build();
        DynamicModel expectedTlOms = DynamicTwoLevelOverloadManagementSystemBuilder.of(network, "TwoLevelOverloadManagementSystem")
                .dynamicModelId("BBM_CLA_TWO_LEVELS")
                .parameterSetId("cla")
                .controlledBranch("NHV1_NHV2_1")
                .iMeasurement1("NHV2_NLOAD")
                .iMeasurement1Side(TwoSides.ONE)
                .iMeasurement2("NGEN_NHV1")
                .iMeasurement2Side(TwoSides.TWO)
                .build();

        List<DynamicModel> dynamicModels = new NetworkExtensionSupplier().get(network);
        assertThat(dynamicModels).hasSize(2).satisfiesExactlyInAnyOrder(
                oms -> assertThat(oms).usingRecursiveComparison().isEqualTo(expectedOms),
                tloms -> assertThat(tloms).usingRecursiveComparison().isEqualTo(expectedTlOms));
    }
}

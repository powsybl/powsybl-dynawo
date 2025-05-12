/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystem;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystem;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystem;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystem;
import com.powsybl.dynawo.models.buses.InfiniteBus;
import com.powsybl.dynawo.models.buses.StandardBus;
import com.powsybl.dynawo.models.generators.*;
import com.powsybl.dynawo.models.loads.*;
import com.powsybl.dynawo.models.hvdc.BaseHvdc;
import com.powsybl.dynawo.models.hvdc.HvdcDangling;
import com.powsybl.dynawo.models.lines.StandardLine;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensator;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsSupplierTest extends AbstractModelSupplierTest {

    private static final List<DynamicModelGroovyExtension> EXTENSIONS = GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME);

    @Test
    void testLibsInfo() {
        for (DynamicModelGroovyExtension extension : EXTENSIONS) {
            assertNotNull(extension.getModelNames());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipmentModelData")
    void testEquipmentDynamicModels(String groovyScriptName, Class<? extends EquipmentBlackBoxModel> modelClass, Network network, String staticId, String parameterId, String lib) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertEquipmentBlackBoxModel(modelClass.cast(dynamicModels.get(0)), staticId, parameterId, lib);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAutomationSystemModelData")
    void testAutomationSystemDynamicModels(String groovyScriptName, Class<? extends BlackBoxModel> modelClass, Network network, String dynamicId, String parameterId, String lib) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertPureDynamicBlackBoxModel(modelClass.cast(dynamicModels.get(0)), dynamicId, parameterId, lib);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideWarningsModel")
    void testDslWarnings(String groovyScriptName, Network network, String report) throws IOException {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        assertTrue(supplier.get(network, reportNode).isEmpty());
        checkReportNode(report);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideGenerator")
    void testGeneratorPrefixes(String groovyScriptName, Class<? extends GeneratorModel> modelClass, Network network, String terminalVarName, String report) throws IOException {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network, reportNode);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertEquals(terminalVarName, modelClass.cast(dynamicModels.get(0)).getTerminalVarName());
        checkReportNode(report);
    }

    void assertEquipmentBlackBoxModel(EquipmentBlackBoxModel bbm, String id, String parameterId, String lib) {
        assertEquals(id, bbm.getDynamicModelId());
        assertEquals(parameterId, bbm.getParameterSetId());
        assertEquals(lib, bbm.getLib());
    }

    void assertPureDynamicBlackBoxModel(BlackBoxModel bbm, String dynamicId, String parameterId, String lib) {
        assertEquals(dynamicId, bbm.getDynamicModelId());
        assertEquals(parameterId, bbm.getParameterSetId());
        assertEquals(lib, bbm.getLib());
    }

    private static Stream<Arguments> provideEquipmentModelData() {
        return Stream.of(
                Arguments.of("/dynamicModels/bus.groovy", StandardBus.class, EurostagTutorialExample1Factory.create(), "NGEN", "SB", "Bus"),
                Arguments.of("/dynamicModels/hvdcP.groovy", BaseHvdc.class, HvdcTestNetwork.createVsc(), "L", "HVDC", "HvdcPV"),
                Arguments.of("/dynamicModels/hvdcVsc.groovy", BaseHvdc.class, HvdcTestNetwork.createVsc(), "L", "HVDC", "HvdcVsc"),
                Arguments.of("/dynamicModels/hvdcPDangling.groovy", HvdcDangling.class, HvdcTestNetwork.createVsc(), "L", "HVDC", "HvdcPVDanglingDiagramPQ"),
                Arguments.of("/dynamicModels/hvdcVscDangling.groovy", HvdcDangling.class, HvdcTestNetwork.createVsc(), "L", "HVDC", "HvdcVscDanglingUdc"),
                Arguments.of("/dynamicModels/loadAB.groovy", BaseLoad.class, EurostagTutorialExample1Factory.create(), "LOAD", "LAB", "LoadAlphaBetaRestorative"),
                Arguments.of("/dynamicModels/loadABControllable.groovy", BaseLoadControllable.class, EurostagTutorialExample1Factory.create(), "LOAD", "LAB", "LoadAlphaBeta"),
                Arguments.of("/dynamicModels/loadTransformer.groovy", LoadOneTransformer.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOT", "LoadOneTransformer"),
                Arguments.of("/dynamicModels/loadTransformerTapChanger.groovy", LoadOneTransformerTapChanger.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOT", "LoadOneTransformerTapChanger"),
                Arguments.of("/dynamicModels/loadTwoTransformers.groovy", LoadTwoTransformers.class, EurostagTutorialExample1Factory.create(), "LOAD", "LTT", "LoadTwoTransformers"),
                Arguments.of("/dynamicModels/loadTwoTransformersTapChangers.groovy", LoadTwoTransformersTapChangers.class, EurostagTutorialExample1Factory.create(), "LOAD", "LTT", "LoadTwoTransformersTapChangers"),
                Arguments.of("/dynamicModels/infiniteBus.groovy", InfiniteBus.class, HvdcTestNetwork.createVsc(), "B1", "b", "InfiniteBusWithVariations"),
                Arguments.of("/dynamicModels/line.groovy", StandardLine.class, EurostagTutorialExample1Factory.create(), "NHV1_NHV2_1", "LINE", "Line"),
                Arguments.of("/dynamicModels/genFictitious.groovy", BaseGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "GF", "GeneratorFictitious"),
                Arguments.of("/dynamicModels/gen.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "GSFWPR", "GeneratorSynchronousThreeWindings"),
                Arguments.of("/dynamicModels/genControllable.groovy", SynchronousGeneratorControllable.class, EurostagTutorialExample1Factory.create(), "GEN", "GSFWPR", "GeneratorSynchronousFourWindingsProportionalRegulations"),
                Arguments.of("/dynamicModels/omegaGen.groovy", SynchronizedGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "GPQ", "GeneratorPQ"),
                Arguments.of("/dynamicModels/omegaGenControllable.groovy", SynchronizedGeneratorControllable.class, EurostagTutorialExample1Factory.create(), "GEN", "GPQ", "GeneratorPV"),
                Arguments.of("/dynamicModels/transformer.groovy", TransformerFixedRatio.class, EurostagTutorialExample1Factory.create(), "NGEN_NHV1", "TFR", "TransformerFixedRatio"),
                Arguments.of("/dynamicModels/svarc.groovy", BaseStaticVarCompensator.class, SvcTestCaseFactory.create(), "SVC2", "svarc", "StaticVarCompensatorPV"),
                Arguments.of("/dynamicModels/wecc.groovy", WeccGen.class, EurostagTutorialExample1Factory.create(), "GEN", "Wind", "WT4BWeccCurrentSource"),
                Arguments.of("/dynamicModels/weccSynchro.groovy", SynchronizedWeccGen.class, EurostagTutorialExample1Factory.create(), "GEN", "Wind", "WTG4AWeccCurrentSource"),
                Arguments.of("/dynamicModels/gridFormingConverter.groovy", GridFormingConverter.class, EurostagTutorialExample1Factory.create(), "GEN", "GF", "GridFormingConverterMatchingControl")
        );
    }

    private static Stream<Arguments> provideAutomationSystemModelData() {
        return Stream.of(
                Arguments.of("/dynamicModels/overloadManagement.groovy", DynamicOverloadManagementSystem.class, EurostagTutorialExample1Factory.create(), "AM_NHV1_NHV2_1", "CLA", "CurrentLimitAutomaton"),
                Arguments.of("/dynamicModels/overloadManagementTwoLevel.groovy", DynamicTwoLevelOverloadManagementSystem.class, EurostagTutorialExample1Factory.create(), "AM_NHV1_NHV2_1", "CLA", "CurrentLimitAutomatonTwoLevels"),
                Arguments.of("/dynamicModels/tapChanger.groovy", TapChangerAutomationSystem.class, EurostagTutorialExample1Factory.create(), "TC", "tc", "TapChangerAutomaton"),
                Arguments.of("/dynamicModels/tapChangerBlockingBusBar.groovy", TapChangerBlockingAutomationSystem.class, FourSubstationsNodeBreakerFactory.create(), "ZAB", "ZAB", "TapChangerBlockingAutomaton2"),
                Arguments.of("/dynamicModels/tapChangerBlocking.groovy", TapChangerBlockingAutomationSystem.class, EurostagTutorialExample1Factory.createWithLFResults(), "ZAB", "ZAB", "TapChangerBlockingAutomaton3"),
                Arguments.of("/dynamicModels/phaseShifterI.groovy", PhaseShifterIAutomationSystem.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterI"),
                Arguments.of("/dynamicModels/phaseShifterP.groovy", PhaseShifterPAutomationSystem.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterP"),
                Arguments.of("/dynamicModels/underVoltage.groovy", UnderVoltageAutomationSystem.class, EurostagTutorialExample1Factory.create(), "UV_GEN", "uv", "UnderVoltageAutomaton")
        );
    }

    private static Stream<Arguments> provideWarningsModel() {
        return Stream.of(
                Arguments.of("/warnings/missingStaticId.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model LoadAlphaBeta null instantiation KO
                                 'staticId' field is not set
                        """),
                Arguments.of("/warnings/missingParameterId.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model LoadAlphaBeta LOAD instantiation KO
                                 'parameterSetId' field is not set
                        """),
                Arguments.of("/warnings/missingEquipment.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model LoadAlphaBeta GEN instantiation KO
                                 'staticId' field value 'GEN' not found for equipment type(s) LOAD
                        """),
                Arguments.of("/warnings/missingDanglingProperty.groovy", HvdcTestNetwork.createVsc(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model HvdcPV L instantiation KO
                                 'dangling' field is set but HvdcPV does not possess this option
                        """),
                Arguments.of("/warnings/underVoltageMissingGenerator.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model UnderVoltage UV_GEN instantiation KO
                                 'generator' field value 'NGEN' not found for equipment type(s) GENERATOR
                        """),
                Arguments.of("/warnings/phaseShifterMissingTransformer.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model PhaseShifterI PS_NGEN_NHV1 instantiation KO
                                 'transformer' field value 'NGEN' not found for equipment type(s) TWO_WINDINGS_TRANSFORMER
                        """),
                Arguments.of("/warnings/claMissingMeasurement.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model OverloadManagementSystem CLA_NGEN instantiation KO
                                 'iMeasurement' field value 'NGEN' not found for equipment type(s) BRANCH
                        """),
                Arguments.of("/warnings/claMissingMeasurementSide.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model OverloadManagementSystem CLA_NGEN instantiation KO
                                 'iMeasurementSide' field is not set
                        """),
                Arguments.of("/warnings/claMissingControlled.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model OverloadManagementSystem CLA_NGEN instantiation KO
                                 'controlledBranch' field value 'GEN' not found for equipment type(s) BRANCH
                        """),
                Arguments.of("/warnings/cla2MissingMeasurement2.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model TwoLevelOverloadManagementSystem CLA_NGEN instantiation KO
                                 'iMeasurement2' field value 'NGEN' not found for equipment type(s) BRANCH
                        """),
                Arguments.of("/warnings/cla2MissingMeasurementSide2.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model TwoLevelOverloadManagementSystem CLA_NGEN instantiation KO
                                 'iMeasurement2Side' field is not set
                        """),
                Arguments.of("/warnings/tapChangerMissingBus.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model TapChangerBlockingAutomaton ZAB instantiation KO
                                 'uMeasurements' field value 'LOAD' not found for equipment type(s) BUS/BUSBAR_SECTION
                                 'uMeasurements' field value 'Wrong_ID' not found for equipment type(s) BUS/BUSBAR_SECTION
                                 'uMeasurements' list is empty
                        """),
                Arguments.of("/warnings/tapChangerMissingBusList.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model TapChangerBlockingAutomaton ZAB instantiation KO
                                 'uMeasurements' field value '[LOAD, Wrong_ID]' not found for equipment type(s) BUS/BUSBAR_SECTION
                                 'uMeasurements' field value '[NGEN_NHV1]' not found for equipment type(s) BUS/BUSBAR_SECTION
                                 'uMeasurements' list is empty
                        """),
                Arguments.of("/warnings/tapChangerCompatible.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model TapChangerBlockingAutomaton ZAB instantiation KO
                                 'transformers' field value 'GEN' not found for equipment type(s) TWO_WINDINGS_TRANSFORMER/LOAD, id will be used as pure dynamic model id
                                 'uMeasurements' field value 'GEN' not found for equipment type(s) BUS/BUSBAR_SECTION
                                 'uMeasurements' list is empty
                        """),
                Arguments.of("/warnings/hvdcVscWrongStaticType.groovy", HvdcTestNetwork.createLcc(),
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              + Model HvdcVsc L instantiation KO
                                 'staticId' field value 'L' not found for equipment type(s) VSC HVDC_LINE
                        """)
                );
    }

    private static Stream<Arguments> provideGenerator() {
        return Stream.of(
                Arguments.of("/dynamicModels/gen.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "generator_terminal",
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              Model GeneratorSynchronousThreeWindings GEN instantiation OK
                        """),
                Arguments.of("/dynamicModels/genTfo.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "transformer_terminal1",
                        """
                        + DSL tests
                           + Groovy Dynamic Models Supplier
                              Model GeneratorSynchronousThreeWindingsPmConstVRNordicTfo GEN instantiation OK
                        """)
        );
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.EquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.automatons.*;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton;
import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcP;
import com.powsybl.dynawaltz.models.hvdc.HvdcPDangling;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.svarcs.StaticVarCompensator;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.dynawaltz.models.generators.GridFormingConverter;
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen;
import com.powsybl.dynawaltz.models.generators.WeccGen;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsSupplierTest extends AbstractModelSupplierTest {

    private static final List<DynamicModelGroovyExtension> EXTENSIONS = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipmentModelData")
    void testEquipmentDynamicModels(String groovyScriptName, Class<? extends EquipmentBlackBoxModel> modelClass, Network network, String staticId, String dynamicId, String parameterId, String lib) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertEquipmentBlackBoxModel(modelClass.cast(dynamicModels.get(0)), dynamicId, staticId, parameterId, lib);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAutomatonModelData")
    void testAutomatonDynamicModels(String groovyScriptName, Class<? extends BlackBoxModel> modelClass, Network network, String dynamicId, String parameterId, String lib) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertPureDynamicBlackBoxModel(modelClass.cast(dynamicModels.get(0)), dynamicId, parameterId, lib);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideWarningsModel")
    void testDslWarnings(String groovyScriptName, Network network, String report) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        assertTrue(supplier.get(network, reporter).isEmpty());
        checkReporter(report);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideGenerator")
    void testGeneratorPrefixes(String groovyScriptName, Class<? extends GeneratorModel> modelClass, Network network, String terminalVarName, String report) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network, reporter);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertEquals(terminalVarName, modelClass.cast(dynamicModels.get(0)).getTerminalVarName());
        checkReporter(report);

    }

    void assertEquipmentBlackBoxModel(EquipmentBlackBoxModel bbm, String dynamicId, String staticId, String parameterId, String lib) {
        assertEquals(dynamicId, bbm.getDynamicModelId());
        assertEquals(staticId, bbm.getStaticId());
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
                Arguments.of("/dynamicModels/bus.groovy", StandardBus.class, EurostagTutorialExample1Factory.create(), "NGEN", "BBM_NGEN", "SB", "Bus"),
                Arguments.of("/dynamicModels/hvdcP.groovy", HvdcP.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcPV"),
                Arguments.of("/dynamicModels/hvdcVsc.groovy", HvdcVsc.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcVSC"),
                Arguments.of("/dynamicModels/hvdcPDangling.groovy", HvdcPDangling.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcPVDanglingDiagramPQ"),
                Arguments.of("/dynamicModels/hvdcVscDangling.groovy", HvdcVscDangling.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcVSCDanglingUdc"),
                Arguments.of("/dynamicModels/loadAB.groovy", BaseLoad.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LAB", "LoadAlphaBetaRestorative"),
                Arguments.of("/dynamicModels/loadABControllable.groovy", BaseLoadControllable.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LAB", "LoadAlphaBeta"),
                Arguments.of("/dynamicModels/loadTransformer.groovy", LoadOneTransformer.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LOT", "LoadOneTransformer"),
                Arguments.of("/dynamicModels/loadTransformerTapChanger.groovy", LoadOneTransformerTapChanger.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LOT", "LoadOneTransformerTapChanger"),
                Arguments.of("/dynamicModels/loadTwoTransformers.groovy", LoadTwoTransformers.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LTT", "LoadTwoTransformers"),
                Arguments.of("/dynamicModels/loadTwoTransformersTapChangers.groovy", LoadTwoTransformersTapChangers.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LTT", "LoadTwoTransformersTapChangers"),
                Arguments.of("/dynamicModels/infiniteBus.groovy", InfiniteBus.class, HvdcTestNetwork.createVsc(), "B1", "BBM_BUS", "b", "InfiniteBusWithVariations"),
                Arguments.of("/dynamicModels/line.groovy", StandardLine.class, EurostagTutorialExample1Factory.create(), "NHV1_NHV2_1", "BBM_NHV1_NHV2_1", "LINE", "Line"),
                Arguments.of("/dynamicModels/genFictitious.groovy", GeneratorFictitious.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GF", "GeneratorFictitious"),
                Arguments.of("/dynamicModels/gen.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GSFWPR", "GeneratorSynchronousThreeWindings"),
                Arguments.of("/dynamicModels/genControllable.groovy", SynchronousGeneratorControllable.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GSFWPR", "GeneratorSynchronousFourWindingsProportionalRegulations"),
                Arguments.of("/dynamicModels/omegaGen.groovy", SynchronizedGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GPQ", "GeneratorPQ"),
                Arguments.of("/dynamicModels/omegaGenControllable.groovy", SynchronizedGeneratorControllable.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GPQ", "GeneratorPV"),
                Arguments.of("/dynamicModels/transformer.groovy", TransformerFixedRatio.class, EurostagTutorialExample1Factory.create(), "NGEN_NHV1", "BBM_NGEN_NHV1", "TFR", "TransformerFixedRatio"),
                Arguments.of("/dynamicModels/svarc.groovy", StaticVarCompensator.class, SvcTestCaseFactory.create(), "SVC2", "BBM_SVARC", "svarc", "StaticVarCompensatorPV"),
                Arguments.of("/dynamicModels/wecc.groovy", WeccGen.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_WT", "Wind", "WT4BWeccCurrentSource"),
                Arguments.of("/dynamicModels/weccSynchro.groovy", SynchronizedWeccGen.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_WT", "Wind", "WTG4AWeccCurrentSource"),
                Arguments.of("/dynamicModels/gridFormingConverter.groovy", GridFormingConverter.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GFC", "GF", "GridFormingConverterMatchingControl")
        );
    }

    private static Stream<Arguments> provideAutomatonModelData() {
        return Stream.of(
                Arguments.of("/dynamicModels/currentLimit.groovy", CurrentLimitAutomaton.class, EurostagTutorialExample1Factory.create(), "AM_NHV1_NHV2_1", "CLA", "CurrentLimitAutomaton"),
                Arguments.of("/dynamicModels/currentLimitTwoLevels.groovy", CurrentLimitTwoLevelsAutomaton.class, EurostagTutorialExample1Factory.create(), "AM_NHV1_NHV2_1", "CLA", "CurrentLimitAutomatonTwoLevels"),
                Arguments.of("/dynamicModels/tapChanger.groovy", TapChangerAutomaton.class, EurostagTutorialExample1Factory.create(), "TC", "tc", "TapChangerAutomaton"),
                Arguments.of("/dynamicModels/tapChangerBlockingBusBar.groovy", TapChangerBlockingAutomaton.class, FourSubstationsNodeBreakerFactory.create(), "ZAB", "ZAB", "TapChangerBlockingAutomaton2"),
                Arguments.of("/dynamicModels/tapChangerBlocking.groovy", TapChangerBlockingAutomaton.class, EurostagTutorialExample1Factory.create(), "ZAB", "ZAB", "TapChangerBlockingAutomaton3"),
                Arguments.of("/dynamicModels/phaseShifterI.groovy", PhaseShifterIAutomaton.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterI"),
                Arguments.of("/dynamicModels/phaseShifterP.groovy", PhaseShifterPAutomaton.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterP"),
                Arguments.of("/dynamicModels/underVoltage.groovy", UnderVoltageAutomaton.class, EurostagTutorialExample1Factory.create(), "UV_GEN", "uv", "UnderVoltageAutomaton")
        );
    }

    private static Stream<Arguments> provideWarningsModel() {
        return Stream.of(
                Arguments.of("/warnings/missingStaticId.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for LoadAlphaBeta
                               'staticId' field is not set
                               'dynamicModelId' field is not set, staticId (unknown staticId) will be used instead
                               Model unknownDynamicId cannot be instantiated
                        """),
                Arguments.of("/warnings/missingParameterId.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for LoadAlphaBeta
                               'parameterSetId' field is not set
                               'dynamicModelId' field is not set, staticId LOAD will be used instead
                               Model LOAD cannot be instantiated
                        """),
                Arguments.of("/warnings/missingEquipment.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for LoadAlphaBeta
                               'staticId' field value 'GEN' not found for equipment type(s) LOAD
                               'dynamicModelId' field is not set, staticId GEN will be used instead
                               Model GEN cannot be instantiated
                        """),
                Arguments.of("/warnings/missingDangling.groovy", HvdcTestNetwork.createVsc(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for HvdcPVDangling
                               'dangling' field is not set
                               Model BBM_HVDC_L cannot be instantiated
                        """),
                Arguments.of("/warnings/missingDanglingProperty.groovy", HvdcTestNetwork.createVsc(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for HvdcPV
                               'dangling' field is set but HvdcPV does not possess this option
                               Model BBM_HVDC_L cannot be instantiated
                        """),
                Arguments.of("/warnings/underVoltageMissingGenerator.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for UnderVoltage
                               'generator' field value 'NGEN' not found for equipment type(s) GENERATOR
                               Model UV_GEN cannot be instantiated
                        """),
                Arguments.of("/warnings/phaseShifterMissingTransformer.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for PhaseShifterI
                               'transformer' field value 'NGEN' not found for equipment type(s) TWO_WINDINGS_TRANSFORMER
                               Model PS_NGEN_NHV1 cannot be instantiated
                        """),
                Arguments.of("/warnings/claMissingMeasurement.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for CurrentLimitAutomaton
                               'iMeasurement' field value 'NGEN' not found for equipment type(s) Quadripole
                               Model CLA_NGEN cannot be instantiated
                        """),
                Arguments.of("/warnings/claMissingMeasurementSide.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for CurrentLimitAutomaton
                               'iMeasurementSide' field is not set
                               Model CLA_NGEN cannot be instantiated
                        """),
                Arguments.of("/warnings/claMissingControlled.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for CurrentLimitAutomaton
                               'controlledQuadripole' field value 'GEN' not found for equipment type(s) Quadripole
                               Model CLA_NGEN cannot be instantiated
                        """),
                Arguments.of("/warnings/cla2MissingMeasurement2.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for CurrentLimitAutomatonTwoLevels
                               'iMeasurement2' field value 'NGEN' not found for equipment type(s) Quadripole
                               Model CLA_NGEN cannot be instantiated
                        """),
                Arguments.of("/warnings/cla2MissingMeasurementSide2.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for CurrentLimitAutomatonTwoLevels
                               'iMeasurement2Side' field is not set
                               Model CLA_NGEN cannot be instantiated
                        """),
                Arguments.of("/warnings/tapChangerMissingBus.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for TapChangerBlockingAutomaton
                               'uMeasurements' field value 'LOAD' not found for equipment type(s) BUS/BUSBAR_SECTION
                               'uMeasurements' field value 'Wrong_ID' not found for equipment type(s) BUS/BUSBAR_SECTION
                               'uMeasurements' field is not set
                               Model ZAB cannot be instantiated
                        """),
                Arguments.of("/warnings/tapChangerMissingBusList.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for TapChangerBlockingAutomaton
                               'uMeasurements' field value 'LOAD' not found for equipment type(s) BUS/BUSBAR_SECTION
                               'uMeasurements' field value 'Wrong_ID' not found for equipment type(s) BUS/BUSBAR_SECTION
                               'uMeasurements' field value 'NGEN_NHV1' not found for equipment type(s) BUS/BUSBAR_SECTION
                               'uMeasurements' field is not set
                               Model ZAB cannot be instantiated
                        """),
                Arguments.of("/warnings/tapChangerCompatible.groovy", EurostagTutorialExample1Factory.create(),
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for TapChangerBlockingAutomaton
                               'uMeasurements' field value 'GEN' not found for equipment type(s) LOAD/TWO_WINDINGS_TRANSFORMER
                               'transformers' list is empty
                               Model ZAB cannot be instantiated
                        """)
                );
    }

    private static Stream<Arguments> provideGenerator() {
        return Stream.of(
                Arguments.of("/dynamicModels/gen.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "generator_terminal",
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for GeneratorSynchronousThreeWindings
                               Model BBM_GEN instantiation successful
                        """),
                Arguments.of("/dynamicModels/genTfo.groovy", SynchronousGenerator.class, EurostagTutorialExample1Factory.create(), "transformer_terminal1",
                        """
                        + DSL tests
                          + Groovy Dynamic Models Supplier
                            + DSL model builder for GeneratorSynchronousThreeWindingsPmConstVRNordicTfo
                               Model BBM_GEN instantiation successful
                        """)
        );
    }
}

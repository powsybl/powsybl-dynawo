/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.dsl.DslException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.EquipmentBlackBoxModelModel;
import com.powsybl.dynawaltz.models.automatons.*;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton;
import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcPv;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.svcs.StaticVarCompensator;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.dynawaltz.models.generators.GridFormingConverter;
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen;
import com.powsybl.dynawaltz.models.generators.WeccGen;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DynamicModelsSupplierTest extends AbstractModelSupplierTest {

    private static final List<DynamicModelGroovyExtension> EXTENSIONS = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipmentModelData")
    void testEquipmentDynamicModels(String groovyScriptName, Class<? extends EquipmentBlackBoxModelModel> modelClass, Network network, String staticId, String dynamicId, String parameterId, String lib) {
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
    @MethodSource("provideExceptionsModel")
    void testDslExceptions(String groovyScriptName, Network network, String exceptionMessage) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        Exception e = assertThrows(DslException.class, () -> supplier.get(network));
        assertEquals(exceptionMessage, e.getMessage());
    }

    void assertEquipmentBlackBoxModel(EquipmentBlackBoxModelModel bbm, String dynamicId, String staticId, String parameterId, String lib) {
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
                Arguments.of("/dynamicModels/hvdcPv.groovy", HvdcPv.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcPV"),
                Arguments.of("/dynamicModels/hvdcVsc.groovy", HvdcVsc.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcVSC"),
                Arguments.of("/dynamicModels/loadAB.groovy", LoadAlphaBeta.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LAB", "LoadAlphaBetaRestorative"),
                Arguments.of("/dynamicModels/loadABControllable.groovy", LoadAlphaBetaControllable.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LAB", "LoadAlphaBeta"),
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
                Arguments.of("/dynamicModels/svc.groovy", StaticVarCompensator.class, SvcTestCaseFactory.create(), "SVC2", "BBM_SVC", "svc", "StaticVarCompensatorPV"),
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
                Arguments.of("/dynamicModels/tapChangerBlocking.groovy", TapChangerBlockingAutomaton.class, EurostagTutorialExample1Factory.create(), "ZAB", "ZAB", "TapChangerBlockingAutomaton1"),
                Arguments.of("/dynamicModels/phaseShifterI.groovy", PhaseShifterIAutomaton.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterI"),
                Arguments.of("/dynamicModels/phaseShifterP.groovy", PhaseShifterPAutomaton.class, EurostagTutorialExample1Factory.create(), "PS_NGEN_NHV1", "ps", "PhaseShifterP"),
                Arguments.of("/dynamicModels/underVoltage.groovy", UnderVoltageAutomaton.class, EurostagTutorialExample1Factory.create(), "UV_GEN", "uv", "UnderVoltageAutomaton")
        );
    }

    private static Stream<Arguments> provideExceptionsModel() {
        return Stream.of(
                Arguments.of("/dynamicModels/currentLimitQuadripoleException.groovy", EurostagTutorialExample1Factory.create(), "I measurement equipment NGEN is not a quadripole"),
                Arguments.of("/dynamicModels/currentLimitMissingControlledException.groovy", EurostagTutorialExample1Factory.create(), "'controlledEquipment' field is not set"),
                Arguments.of("/dynamicModels/phaseShifterTransformerException.groovy", EurostagTutorialExample1Factory.create(), "Transformer static id unknown: NGEN"),
                Arguments.of("/dynamicModels/tapChangerBusException.groovy", EurostagTutorialExample1Factory.create(), "Bus static id unknown: LOAD"),
                Arguments.of("/dynamicModels/tapChangerCompatibleException.groovy", EurostagTutorialExample1Factory.create(), "GENERATOR GEN is not compatible"),
                Arguments.of("/dynamicModels/underVoltageGeneratorException.groovy", EurostagTutorialExample1Factory.create(), "Generator static id unknown: NGEN")
        );
    }
}

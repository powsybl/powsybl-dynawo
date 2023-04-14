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
import com.powsybl.dynawaltz.models.EquipmentBlackBoxModelModel;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous;
import com.powsybl.dynawaltz.models.generators.OmegaRefGenerator;
import com.powsybl.dynawaltz.models.hvdc.HvdcModel;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.dynawaltz.models.loads.LoadAlphaBeta;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer;
import com.powsybl.dynawaltz.models.svcs.StaticVarCompensatorModel;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DynamicModelsSupplierTest extends AbstractModelSupplierTest {

    private static final String FOLDER_NAME = "/dynamicModels/";
    private static final List<DynamicModelGroovyExtension> EXTENSIONS = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @Test
    void testGroovyExtensionCount() {
        assertEquals(10, EXTENSIONS.size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipmentModelData")
    void testEquipmentDynamicModels(String groovyScriptName, Class<? extends EquipmentBlackBoxModelModel> modelClass, Network network, String staticId, String dynamicId, String parameterId, String lib) {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(network);
        assertEquals(1, dynamicModels.size());
        assertTrue(modelClass.isInstance(dynamicModels.get(0)));
        assertEquipmentBlackBoxModel(modelClass.cast(dynamicModels.get(0)), dynamicId, staticId, parameterId, lib);
    }

    @Test
    void testCurrentLimitAutomaton() {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream("currentLimit"), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(EurostagTutorialExample1Factory.create());
        assertEquals(1, dynamicModels.size());
        assertTrue(dynamicModels.get(0) instanceof CurrentLimitAutomaton);
        CurrentLimitAutomaton bbm = (CurrentLimitAutomaton) dynamicModels.get(0);
        assertEquals("AM_NHV1_NHV2_1", bbm.getDynamicModelId());
        assertEquals("CLA", bbm.getParameterSetId());
        assertEquals("CurrentLimitAutomaton", bbm.getLib());
        assertEquals("NHV1_NHV2_1", bbm.getLineStaticId());
    }

    void assertEquipmentBlackBoxModel(EquipmentBlackBoxModelModel bbm, String dynamicId, String staticId, String parameterId, String lib) {
        assertEquals(dynamicId, bbm.getDynamicModelId());
        assertEquals(staticId, bbm.getStaticId());
        assertEquals(parameterId, bbm.getParameterSetId());
        assertEquals(lib, bbm.getLib());
    }

    private static Stream<Arguments> provideEquipmentModelData() {
        return Stream.of(
                Arguments.of("bus", StandardBus.class, EurostagTutorialExample1Factory.create(), "NGEN", "BBM_NGEN", "SB", "Bus"),
                Arguments.of("loadAB", LoadAlphaBeta.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LAB", "LoadAlphaBeta"),
                Arguments.of("loadTransformer", LoadOneTransformer.class, EurostagTutorialExample1Factory.create(), "LOAD", "LOAD", "LOT", "LoadOneTransformer"),
                Arguments.of("hvdc", HvdcModel.class, HvdcTestNetwork.createVsc(), "L", "BBM_HVDC_L", "HVDC", "HvdcPV"),
                Arguments.of("line", StandardLine.class, EurostagTutorialExample1Factory.create(), "NHV1_NHV2_1", "BBM_NHV1_NHV2_1", "LINE", "Line"),
                Arguments.of("genFictitious", GeneratorFictitious.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GF", "GeneratorFictitious"),
                Arguments.of("gen", GeneratorSynchronous.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GSFWPR", "GeneratorSynchronousFourWindingsProportionalRegulations"),
                Arguments.of("omegaGen", OmegaRefGenerator.class, EurostagTutorialExample1Factory.create(), "GEN", "BBM_GEN", "GPQ", "GeneratorPQ"),
                Arguments.of("transformer", TransformerFixedRatio.class, EurostagTutorialExample1Factory.create(), "NGEN_NHV1", "BBM_NGEN_NHV1", "TFR", "TransformerFixedRatio"),
                Arguments.of("svc", StaticVarCompensatorModel.class, SvcTestCaseFactory.create(), "SVC2", "BBM_SVC", "svc", "StaticVarCompensatorPV")
        );
    }

    @Override
    String getFolderName() {
        return FOLDER_NAME;
    }
}

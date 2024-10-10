/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.loads.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class LoadsModelXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";
    private static final String DYN_LOAD_NAME = "BBM_" + LOAD_NAME;

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> loadConstructor) {
        setupNetwork();
        addDynamicModels(loadConstructor);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> loadConstructor) {
        dynamicModels.add(loadConstructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideLoads")
    void writeLoadModel(String dydName, Function< Network, BlackBoxModel> loadConstructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideLoads() {
        return Stream.of(
                Arguments.of("load_alpha_beta_dyd.xml", (Function<Network, BlackBoxModel>) n -> BaseLoadBuilder.of(n, "LoadAlphaBeta")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LAB")
                        .build()),
                Arguments.of("load_one_transformer_dyd.xml", (Function<Network, BlackBoxModel>) n -> LoadOneTransformerBuilder.of(n, "LoadOneTransformer")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LOT")
                        .build()),
                Arguments.of("load_one_transformer_tap_changer_dyd.xml", (Function<Network, BlackBoxModel>) n -> LoadOneTransformerTapChangerBuilder.of(n, "LoadOneTransformerTapChanger")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LOTTC")
                        .build()),
                Arguments.of("load_two_transformers_dyd.xml", (Function<Network, BlackBoxModel>) n -> LoadTwoTransformersBuilder.of(n, "LoadTwoTransformers")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LTT")
                        .build()),
                Arguments.of("load_two_transformers_tap_changers_dyd.xml", (Function<Network, BlackBoxModel>) n -> LoadTwoTransformersTapChangersBuilder.of(n, "LoadTwoTransformersTapChangers")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LTTTC")
                        .build()),
                Arguments.of("load_synchronized.xml", (Function<Network, BlackBoxModel>) n -> BaseLoadBuilder.of(n, "LoadAlphaBetaMotor")
                        .dynamicModelId(DYN_LOAD_NAME)
                        .staticId(LOAD_NAME)
                        .parameterSetId("LAB")
                        .build())
        );
    }
}

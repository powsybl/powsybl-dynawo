/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class LoadsModelXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";
    private static final String DYN_LOAD_NAME = "BBM_" + LOAD_NAME;

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> loadConstructor) {
        setupNetwork();
        addDynamicModels(loadConstructor);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> loadConstructor) {
        dynamicModels.add(loadConstructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideLoads")
    void writeLoadModel(String dydName, Function< Network, BlackBoxModel> loadConstructor) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName + ".xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideLoads() {
        return Stream.of(
                Arguments.of("load_alpha_beta_dyd", (Function<Network, BlackBoxModel>) n -> new LoadAlphaBeta(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LAB")),
                Arguments.of("load_one_transformer_dyd", (Function<Network, BlackBoxModel>) n -> new LoadOneTransformer(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LOT")),
                Arguments.of("load_one_transformer_tap_changer_dyd", (Function<Network, BlackBoxModel>) n -> new LoadOneTransformerTapChanger(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LOTTC")),
                Arguments.of("load_two_transformers_dyd", (Function<Network, BlackBoxModel>) n -> new LoadTwoTransformers(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LTT")),
                Arguments.of("load_two_transformers_tap_changers_dyd", (Function<Network, BlackBoxModel>) n -> new LoadTwoTransformersTapChangers(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LTTTC"))
        );
    }
}

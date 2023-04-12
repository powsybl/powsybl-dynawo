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
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class LoadsModelXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";
    private static final String DYN_LOAD_NAME = "BBM_" + LOAD_NAME;

    @BeforeEach
    void setup(String dydName, BlackBoxModel bbm) {
        setupNetwork();
        addDynamicModels(bbm);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(BlackBoxModel bbm) {
        dynamicModels.add(bbm);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideLoads")
    void writeLoadModel(String dydName, BlackBoxModel bbm) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName + ".xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideLoads() {
        return Stream.of(
                Arguments.of("load_alpha_beta_dyd", new LoadAlphaBeta(DYN_LOAD_NAME, LOAD_NAME, "LAB")),
                Arguments.of("load_one_transformer_dyd", new LoadOneTransformer(DYN_LOAD_NAME, LOAD_NAME, "LOT")),
                Arguments.of("load_one_transformer_tap_changer_dyd", new LoadOneTransformerTapChanger(DYN_LOAD_NAME, LOAD_NAME, "LOTTC")),
                Arguments.of("load_two_transformers_dyd", new LoadTwoTransformers(DYN_LOAD_NAME, LOAD_NAME, "LTT")),
                Arguments.of("load_two_transformers_tap_changers_dyd", new LoadTwoTransformersTapChangers(DYN_LOAD_NAME, LOAD_NAME, "LTTTC"))
        );
    }
}

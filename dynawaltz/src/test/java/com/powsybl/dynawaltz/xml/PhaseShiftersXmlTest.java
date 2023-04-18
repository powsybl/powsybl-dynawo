/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
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
class PhaseShiftersXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String PHASE_SHIFTER_NAME = "phase_shifter";
    private static final String DYN_NAME = "BBM_" + PHASE_SHIFTER_NAME;

    @BeforeEach
    void setup(String dydName, Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) {
        setupNetwork();
        addDynamicModels(phaseShifterConstructor, dynamicTransformer);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) {
        dynamicModels.add(phaseShifterConstructor.apply(network));
        if (dynamicTransformer) {
            dynamicModels.add(new TransformerFixedRatio("BBM_NGEN_NHV1", "NGEN_NHV1", "tt", "TransformerFixedRatio"));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("providePhaseShifter")
    void writeLoadModel(String dydName, Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName + ".xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> providePhaseShifter() {
        return Stream.of(
                Arguments.of("phase_shifter_i_dyd", (Function<Network, BlackBoxModel>) n -> new PhaseShifterIAutomaton(DYN_NAME, n.getTwoWindingsTransformer("NGEN_NHV1"), "ps"), true),
                Arguments.of("phase_shifter_p_dyd", (Function<Network, BlackBoxModel>) n -> new PhaseShifterPAutomaton(DYN_NAME, n.getTwoWindingsTransformer("NGEN_NHV1"), "ps"), false)
        );
    }
}

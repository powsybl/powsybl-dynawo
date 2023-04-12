/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class TapChangerAutomatonExceptionsXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";
    private static final String DYN_LOAD_NAME = "BBM_" + LOAD_NAME;

    @BeforeEach
    void setup(TransformerSide side, BlackBoxModel load, String exceptionMessage) {
        setupNetwork();
        addDynamicModels(side, load);
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(TransformerSide side, BlackBoxModel load) {
        dynamicModels.add(load);
        dynamicModels.add(new TapChangerAutomaton("BBM_TC", "tc", network.getLoad(LOAD_NAME), side));
    }

    @ParameterizedTest
    @MethodSource("provideTapChangers")
    void testExceptions(TransformerSide side, BlackBoxModel load, String exceptionMessage) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals(exceptionMessage, e.getMessage());
    }

    private static Stream<Arguments> provideTapChangers() {
        return Stream.of(
                Arguments.of(TransformerSide.HIGH_VOLTAGE, new LoadOneTransformer(DYN_LOAD_NAME, LOAD_NAME, "LOT"), "LoadOneTransformer doesn't have a transformer side"),
                Arguments.of(TransformerSide.NONE, new LoadOneTransformerTapChanger(DYN_LOAD_NAME, LOAD_NAME, "LOTTC"), "LoadOneTransformerTapChanger already have a tap changer"),
                Arguments.of(TransformerSide.NONE, new LoadTwoTransformers(DYN_LOAD_NAME, LOAD_NAME, "LTT"), "LoadTwoTransformers must have a side connected to the Tap changer automaton"),
                Arguments.of(TransformerSide.HIGH_VOLTAGE, new LoadTwoTransformersTapChangers(DYN_LOAD_NAME, LOAD_NAME, "LTTTC"), "LoadTwoTransformersTapChangers already have a tap changer")
        );
    }
}

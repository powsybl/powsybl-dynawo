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
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformers;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class TapChangerAutomatonExceptionsXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";
    private static final String DYN_LOAD_NAME = "BBM_" + LOAD_NAME;

    @BeforeEach
    void setup(TransformerSide side, Function<Network, BlackBoxModel> loadConstructor, String exceptionMessage) {
        setupNetwork();
        addDynamicModels(side, loadConstructor);
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(TransformerSide side, Function< Network, BlackBoxModel> loadConstructor) {
        dynamicModels.add(loadConstructor.apply(network));
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId(LOAD_NAME)
                .side(side)
                .build());
    }

    @ParameterizedTest
    @MethodSource("provideTapChangers")
    void testExceptions(TransformerSide side, Function< Network, BlackBoxModel> loadConstructor, String exceptionMessage) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals(exceptionMessage, e.getMessage());
    }

    private static Stream<Arguments> provideTapChangers() {
        return Stream.of(
                Arguments.of(TransformerSide.HIGH_VOLTAGE, (Function<Network, BlackBoxModel>) n -> new LoadOneTransformer(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LOT", "LoadOneTransformer"), "LoadOneTransformer doesn't have a transformer side"),
                Arguments.of(TransformerSide.NONE, (Function<Network, BlackBoxModel>) n -> new LoadOneTransformerTapChanger(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LOTTC", "LoadOneTransformerTapChanger"), "LoadOneTransformerTapChanger already have a tap changer"),
                Arguments.of(TransformerSide.NONE, (Function<Network, BlackBoxModel>) n -> new LoadTwoTransformers(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LTT", "LoadTwoTransformers"), "LoadTwoTransformers must have a side connected to the Tap changer automaton"),
                Arguments.of(TransformerSide.HIGH_VOLTAGE, (Function<Network, BlackBoxModel>) n -> new LoadTwoTransformersTapChangers(DYN_LOAD_NAME, n.getLoad(LOAD_NAME), "LTTTC", "LoadTwoTransformersTapChangers"), "LoadTwoTransformersTapChangers already have a tap changer")
        );
    }
}

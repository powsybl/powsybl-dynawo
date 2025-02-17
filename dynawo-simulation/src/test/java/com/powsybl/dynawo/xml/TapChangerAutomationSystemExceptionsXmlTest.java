/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerTapChangerBuilder;
import com.powsybl.dynawo.models.loads.LoadTwoTransformersBuilder;
import com.powsybl.dynawo.models.loads.LoadTwoTransformersTapChangersBuilder;
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
class TapChangerAutomationSystemExceptionsXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String LOAD_NAME = "LOAD";

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
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId(LOAD_NAME)
                .side(side)
                .build());
    }

    @ParameterizedTest
    @MethodSource("provideTapChangers")
    void testExceptions(TransformerSide side, Function< Network, BlackBoxModel> loadConstructor, String exceptionMessage) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawoContext);
        assertEquals(exceptionMessage, e.getMessage());
    }

    private static Stream<Arguments> provideTapChangers() {
        return Stream.of(
                Arguments.of(TransformerSide.HIGH_VOLTAGE, (Function<Network, BlackBoxModel>) n -> LoadOneTransformerBuilder.of(n, "LoadOneTransformer")
                        .staticId(LOAD_NAME)
                        .parameterSetId("LOT")
                        .build(),
                        "LoadOneTransformer doesn't have a transformer side"),
                Arguments.of(TransformerSide.NONE, (Function<Network, BlackBoxModel>) n -> LoadOneTransformerTapChangerBuilder.of(n, "LoadOneTransformerTapChanger")
                        .staticId(LOAD_NAME)
                        .parameterSetId("LOTTC")
                        .build(),
                        "LoadOneTransformerTapChanger already have a tap changer"),
                Arguments.of(TransformerSide.NONE, (Function<Network, BlackBoxModel>) n -> LoadTwoTransformersBuilder.of(n, "LoadTwoTransformers")
                        .staticId(LOAD_NAME)
                        .parameterSetId("LTT")
                        .build(),
                        "LoadTwoTransformers must have a side connected to the Tap changer automaton"),
                Arguments.of(TransformerSide.HIGH_VOLTAGE, (Function<Network, BlackBoxModel>) n -> LoadTwoTransformersTapChangersBuilder.of(n, "LoadTwoTransformersTapChangers")
                        .staticId(LOAD_NAME)
                        .parameterSetId("LTTTC")
                        .build(),
                        "LoadTwoTransformersTapChangers already have a tap changer")
        );
    }
}

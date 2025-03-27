/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
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
class PhaseShiftersXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String PHASE_SHIFTER_NAME = "phase_shifter";
    private static final String DYN_NAME = "BBM_" + PHASE_SHIFTER_NAME;

    @BeforeEach
    void setup(String dydName, Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) {
        setupNetwork();
        addDynamicModels(phaseShifterConstructor, dynamicTransformer);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) {
        dynamicModels.add(phaseShifterConstructor.apply(network));
        if (dynamicTransformer) {
            dynamicModels.add(TransformerFixedRatioBuilder.of(network)
                    .dynamicModelId("BBM_NGEN_NHV1")
                    .staticId("NGEN_NHV1")
                    .parameterSetId("tt")
                    .build());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("providePhaseShifter")
    void writeLoadModel(String dydName, Function<Network, BlackBoxModel> phaseShifterConstructor, boolean dynamicTransformer) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> providePhaseShifter() {
        return Stream.of(
                Arguments.of("phase_shifter_i_dyd.xml", (Function<Network, BlackBoxModel>) n ->
                        PhaseShifterIAutomationSystemBuilder.of(n)
                                .dynamicModelId(DYN_NAME)
                                .parameterSetId("ps")
                                .transformer("NGEN_NHV1")
                                .build(), true),
                Arguments.of("phase_shifter_p_dyd.xml", (Function<Network, BlackBoxModel>) n ->
                        PhaseShifterPAutomationSystemBuilder.of(n)
                                .dynamicModelId(DYN_NAME)
                                .parameterSetId("ps")
                                .transformer("NGEN_NHV1")
                                .build(), false));
    }
}

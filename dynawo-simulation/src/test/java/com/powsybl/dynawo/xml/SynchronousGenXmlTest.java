/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
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
class SynchronousGenXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String STATIC_ID = "GEN";

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> loadConstructor) {
        setupNetwork();
        addDynamicModels(loadConstructor);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideGen")
    void writeModel(String dydName, Function< Network, BlackBoxModel> constructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideGen() {
        return Stream.of(
                Arguments.of("synchronous_gen_dyd.xml", (Function<Network, BlackBoxModel>) n ->
                        SynchronousGeneratorBuilder.of(n, "GeneratorSynchronousFourWindingsTGov1Sexs")
                                .staticId(STATIC_ID)
                                .parameterSetId("GSTW")
                                .build()),
                Arguments.of("synchronous_gen_tfo_dyd.xml", (Function<Network, BlackBoxModel>) n ->
                        SynchronousGeneratorBuilder.of(n, "GeneratorSynchronousFourWindingsPmConstVRNordicTfo")
                            .staticId(STATIC_ID)
                            .parameterSetId("GSTW")
                            .build())
        );
    }
}

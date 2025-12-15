/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.builders.AdditionalModelConfigLoader;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class AdditionalGeneratorModelXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String GEN_ID = "GEN";
    private static final String GPQ_PARAM_ID = "GPQ";

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        Path additionalModels = Path.of(Objects.requireNonNull(
                AdditionalGeneratorModelXmlTest.class.getResource("/additionalModelsDyd.json")).toURI());
        ModelConfigsHandler.getInstance().addModels(new AdditionalModelConfigLoader(additionalModels));
    }

    @BeforeEach
    void setup(String dydName, Function<Network, BlackBoxModel> genConstructor) {
        setupNetwork();
        addDynamicModels(genConstructor);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
    }

    protected void addDynamicModels(Function<Network, BlackBoxModel> genConstructor) {
        dynamicModels.add(genConstructor.apply(network));
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network)
                .dynamicModelId("UVA")
                .generator(GEN_ID)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .startTime(1.0)
                .staticId(GEN_ID)
                .build());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideGenerator")
    void writeModel(String dydName, Function<Network, BlackBoxModel> genConstructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideGenerator() {
        return Stream.of(
                Arguments.of("additional_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n ->
                        BaseGeneratorBuilder.of(n, "BaseGenerator2")
                            .staticId(GEN_ID)
                            .parameterSetId(GPQ_PARAM_ID)
                            .build()),
                Arguments.of("additional_synchronized_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n ->
                                SynchronizedGeneratorBuilder.of(n, "SynchronizedGenerator2")
                                        .staticId(GEN_ID)
                                        .parameterSetId(GPQ_PARAM_ID)
                                        .build()),
                Arguments.of("additional_synchronized_controllable_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n ->
                                SynchronizedGeneratorBuilder.of(n, "SynchronizedGenerator3")
                                        .staticId(GEN_ID)
                                        .parameterSetId(GPQ_PARAM_ID)
                                        .build()),
                Arguments.of("additional_synchronous_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n ->
                                SynchronousGeneratorBuilder.of(n, "SynchronousGenerator2")
                                        .staticId(GEN_ID)
                                        .parameterSetId("GSTWPR")
                                        .build()),
                Arguments.of("additional_synchronous_controllable_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n ->
                                SynchronousGeneratorBuilder.of(n, "SynchronousGenerator3")
                                        .staticId(GEN_ID)
                                        .parameterSetId("GSTWPR")
                                        .build())
        );
    }
}

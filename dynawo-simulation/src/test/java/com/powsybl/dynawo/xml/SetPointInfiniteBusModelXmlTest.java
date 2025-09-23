/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynamicModelsConfigUtils;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
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
class SetPointInfiniteBusModelXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String dydName, Function<Network, BlackBoxModel> busConstructor) {
        setupNetwork();
        addDynamicModels(busConstructor);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMoreGenerators();
    }

    protected void addDynamicModels(Function<Network, BlackBoxModel> busConstructor) {
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .staticId("GEN")
                .parameterSetId("pq")
                .build());
        BlackBoxModel bus = busConstructor.apply(network);
        dynamicModels.add(bus);
        if (bus.needMandatoryDynamicModels()) {
            DynamicModelsConfigUtils.mandatoryModelsAdder(network, dynamicModels);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideBus")
    void writeModel(String dydName, Function<Network, BlackBoxModel> busConstructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "set_point_inf_bus_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }

    private static Stream<Arguments> provideBus() {
        return Stream.of(
                Arguments.of("set_point_std_bus_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> StandardBusBuilder.of(n, "Bus")
                                .staticId("NGEN")
                                .parameterSetId("ib")
                                .build()),
                Arguments.of("set_point_inf_bus_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> InfiniteBusBuilder.of(n, "InfiniteBus")
                                .staticId("NGEN")
                                .parameterSetId("ib")
                                .build()),
                Arguments.of("set_point_inf_bus_gen_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> InfiniteBusBuilder.of(n, "InfiniteBus")
                                .staticId("GEN2")
                                .parameterSetId("ib")
                                .build())
        );
    }
}

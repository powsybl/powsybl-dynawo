/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.generators.iec.IecBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.AfterEach;
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
class IecGenXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String STATIC_ID = "GEN";
    private static final DynawoVersion DYNAWO_VERSION = DynawoVersion.createFromString("1.8.0");

    @BeforeEach
    void setup(String dydName, String parName, Function<Network, BlackBoxModel> loadConstructor) {
        ModelConfigsHandler.getInstance().setDynawoVersion(DYNAWO_VERSION);
        setupNetwork();
        addDynamicModels(loadConstructor);
        setupDynawoContext();
    }

    @AfterEach
    @Override
    public void tearDown() {
        ModelConfigsHandler.getInstance().setDynawoVersion(DynawoConstants.CURRENT_VERSION);
    }

    @Override
    protected DynawoSimulationContext.Builder setupDynawoContextBuilder() {
        return super.setupDynawoContextBuilder().currentVersion(DYNAWO_VERSION);
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function<Network, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideIec")
    void writeModel(String dydName, String parName, Function<Network, BlackBoxModel> constructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        if (!parName.isEmpty()) {
            validate("parameters.xsd", parName, tmpDir.resolve(context.getSimulationParFile()));
        }
    }

    private static Stream<Arguments> provideIec() {
        return Stream.of(
                Arguments.of("iec_wt4b_2015_dyd.xml", "", (Function<Network, BlackBoxModel>) n ->
                        IecBuilder.of(n, "IECWT4BCurrentSource2015")
                            .staticId(STATIC_ID)
                            .parameterSetId("WT4B")
                            .build()),
                Arguments.of("iec_wt4a_2020_dyd.xml", "", (Function<Network, BlackBoxModel>) n ->
                        IecBuilder.of(n, "IECWT4ACurrentSource2020")
                            .staticId(STATIC_ID)
                            .parameterSetId("WT4A_2020")
                            .build()),
                Arguments.of("iec_wpp4a_2015_dyd.xml", "", (Function<Network, BlackBoxModel>) n ->
                        IecBuilder.of(n, "IECWPP4ACurrentSource2015")
                            .staticId(STATIC_ID)
                            .parameterSetId("WPP4A")
                            .build()),
                Arguments.of("iec_wt4b_2020_dyd.xml", "", (Function<Network, BlackBoxModel>) n ->
                        IecBuilder.of(n, "IECWT4BCurrentSource2020")
                            .staticId(STATIC_ID)
                            .parameterSetId("WT4B_2020")
                            .build())
        );
    }
}

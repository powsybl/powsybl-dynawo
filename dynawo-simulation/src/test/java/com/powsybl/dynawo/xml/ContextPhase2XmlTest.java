/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.shunts.BaseShuntBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.DYD_FILENAME;
import static com.powsybl.dynawo.xml.DynawoSimulationConstants.PHASE_2_DYD_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class ContextPhase2XmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String dydName, String phase2DydName, Predicate<BlackBoxModel> phase2ModelsPredicate) {
        setupNetwork();
        addDynamicModels();
        setupDynawoContext(phase2ModelsPredicate);
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    protected void addDynamicModels() {
        dynamicModels.add(BaseShuntBuilder.of(network)
                .staticId("SHUNT")
                .parameterSetId("sh")
                .build());
        dynamicModels.add(BaseLoadBuilder.of(network)
                .staticId("LOAD")
                .parameterSetId("lab")
                .build());
        dynamicModels.add(BaseLoadBuilder.of(network)
                .staticId("LOAD2")
                .parameterSetId("lab")
                .build());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideBbm")
    void writePhase2Models(String dydName, String phase2DydName, Predicate<BlackBoxModel> phase2ModelsPredicate) throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DYD_FILENAME));
        assertThat(context.getPhase2DydData()).isPresent();
        DydXml.write(tmpDir, PHASE_2_DYD_FILENAME, context.getPhase2DydData().get());
        validate("dyd.xsd", phase2DydName, tmpDir.resolve(PHASE_2_DYD_FILENAME));
    }

    private static Stream<Arguments> provideBbm() {
        return Stream.of(
                Arguments.of("no_loads_phase_1_dyd.xml", "no_loads_phase_2_dyd.xml",
                        (Predicate<BlackBoxModel>) bbm -> bbm.getLib().equalsIgnoreCase("LoadAlphaBeta")),
                Arguments.of("specific_load_phase_1_dyd.xml", "specific_load_phase_2_dyd.xml",
                        (Predicate<BlackBoxModel>) bbm -> bbm.getDynamicModelId().equalsIgnoreCase("LOAD")));
    }
}

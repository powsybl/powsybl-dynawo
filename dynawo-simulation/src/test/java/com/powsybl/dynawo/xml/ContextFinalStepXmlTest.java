/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.FinalStepConfig;
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

import static com.powsybl.dynawo.DynawoSimulationConstants.DYD_FILENAME;
import static com.powsybl.dynawo.DynawoSimulationConstants.FINAL_STEP_DYD_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class ContextFinalStepXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String dydName, String finalStepDydName, Predicate<BlackBoxModel> finalStepModelsPredicate) {
        setupNetwork();
        addDynamicModels();
        setupDynawoContext(new FinalStepConfig(200, finalStepModelsPredicate));
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
    void writeFinalStepModels(String dydName, String finalStepDydName, Predicate<BlackBoxModel> finalStepModelsPredicate) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", dydName, tmpDir.resolve(DYD_FILENAME));
        assertThat(context.getFinalStepDydData()).isPresent();
        DydXml.write(tmpDir, FINAL_STEP_DYD_FILENAME, context.getFinalStepDydData().get());
        validate("dyd.xsd", finalStepDydName, tmpDir.resolve(FINAL_STEP_DYD_FILENAME));
    }

    private static Stream<Arguments> provideBbm() {
        return Stream.of(
                Arguments.of("no_loads_first_step_dyd.xml", "no_loads_final_step_dyd.xml",
                        (Predicate<BlackBoxModel>) bbm -> bbm.getLib().equalsIgnoreCase("LoadAlphaBeta")),
                Arguments.of("specific_load_first_step_dyd.xml", "specific_load_final_step_dyd.xml",
                        (Predicate<BlackBoxModel>) bbm -> bbm.getDynamicModelId().equalsIgnoreCase("LOAD")));
    }
}

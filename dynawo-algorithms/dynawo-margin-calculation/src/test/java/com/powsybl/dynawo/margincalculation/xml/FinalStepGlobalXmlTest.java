/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.xml.AbstractDynamicModelXmlTest;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.DynawoSimulationConstants.DYD_FILENAME;
import static com.powsybl.dynawo.DynawoSimulationConstants.FINAL_STEP_DYD_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class FinalStepGlobalXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(
                BaseGeneratorBuilder.of(network)
                        .staticId("GEN")
                        .parameterSetId("gen")
                        .build());
        dynamicModels.add(
                BaseLoadBuilder.of(network)
                        .staticId("LOAD")
                        .parameterSetId("lab")
                        .build());
        dynamicModels.add(
                BaseLoadBuilder.of(network)
                        .staticId("LOAD2")
                        .parameterSetId("lab")
                        .build());
    }

    @Override
    protected void setupDynawoContext() {
        MarginCalculationParameters parameters = MarginCalculationParameters.load();
        List<LoadsVariation> loadsVariationList = List.of(
                new LoadsVariation(List.of(network.getLoad("LOAD2"), network.getLoad("LOAD3")), 30));
        context = new MarginCalculationContext.Builder<>(network, dynamicModels, Collections.emptyList(), loadsVariationList)
                .marginCalculationParameters(parameters)
                .build();
    }

    @Test
    void writeDyd() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        assertThat(context.getFinalStepDydData()).isPresent();
        DydXml.write(tmpDir, FINAL_STEP_DYD_FILENAME, context.getFinalStepDydData().get());
        validate("dyd.xsd", "first_step_global_dyd.xml", tmpDir.resolve(DYD_FILENAME));
        validate("dyd.xsd", "final_step_global_dyd.xml", tmpDir.resolve(FINAL_STEP_DYD_FILENAME));
    }
}

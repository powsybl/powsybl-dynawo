/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.dynawo.DynawoSimulationParameters;
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
import static com.powsybl.dynawo.DynawoSimulationConstants.PHASE_2_DYD_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class Phase2HybridXmlTest extends AbstractDynamicModelXmlTest {

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
        MarginCalculationParameters parameters = MarginCalculationParameters.builder()
                .setLoadModelsRule(MarginCalculationParameters.LoadModelsRule.HYBRID).build();
        DynawoSimulationParameters dynawoSimulationParameters = DynawoSimulationParameters.load();
        List<LoadsVariation> loadsVariationList = List.of(
                new LoadsVariation(List.of(network.getLoad("LOAD2"), network.getLoad("LOAD3")), 30));
        context = new MarginCalculationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels,
                parameters, dynawoSimulationParameters, Collections.emptyList(), loadsVariationList);
    }

    @Test
    void writeDyd() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        assertThat(context.getPhase2DydData()).isPresent();
        DydXml.write(tmpDir, PHASE_2_DYD_FILENAME, context.getPhase2DydData().get());
        validate("dyd.xsd", "phase1_hybrid_dyd.xml", tmpDir.resolve(DYD_FILENAME));
        validate("dyd.xsd", "phase2_hybrid_dyd.xml", tmpDir.resolve(PHASE_2_DYD_FILENAME));
    }
}

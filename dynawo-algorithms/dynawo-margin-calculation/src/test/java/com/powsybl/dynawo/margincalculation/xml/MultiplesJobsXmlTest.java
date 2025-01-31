/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.xml.AbstractDynamicModelXmlTest;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class MultiplesJobsXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        // no models
    }

    @Override
    protected void setupDynawoContext() {
        List<Contingency> contingencies = List.of(
                Contingency.load("LOAD"),
                Contingency.builder("DisconnectLineGenerator")
                        .addLine("NHV1_NHV2_1")
                        .addGenerator("GEN2")
                        .build());
        MarginCalculationParameters parameters = MarginCalculationParameters.load();
        List<LoadsVariation> loadsVariationList = List.of(
                new LoadsVariation(List.of(network.getLoad("LOAD"), network.getLoad("LOAD2")), 10));
        context = new MarginCalculationContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, parameters, contingencies, loadsVariationList);
    }

    @Test
    void writeMultiplesJobs() throws SAXException, IOException, XMLStreamException {
        MultipleJobsXml.write(tmpDir, (MarginCalculationContext) context);
        validate("multipleJobs.xsd", "multipleJobs_mc.xml", tmpDir.resolve(MULTIPLE_JOBS_FILENAME));
    }
}

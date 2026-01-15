/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.xml;

import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventData;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationContext;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import com.powsybl.dynawo.xml.AbstractDynamicModelXmlTest;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
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
        CriticalTimeCalculationParameters parameters = CriticalTimeCalculationParameters.load();
        List<NodeFaultEventData> nodeFaultsList = List.of(
                new NodeFaultEventData.Builder(network)
                        .setStaticId("NGEN")
                        .setFaultStartTime(1)
                        .setFaultStopTime(5)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build(),
                new NodeFaultEventData.Builder(network)
                        .setStaticId("NLOAD")
                        .setFaultStartTime(1)
                        .setFaultStopTime(5)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build()
        );
        context = new CriticalTimeCalculationContext.Builder(network, dynamicModels, nodeFaultsList)
                .criticalTimeCalculationParameters(parameters)
                .build();
    }

    @Test
    void writeMultiplesJobs() throws SAXException, IOException, XMLStreamException {
        MultipleJobsXml.write(tmpDir, (CriticalTimeCalculationContext) context);
        validate("multipleJobs.xsd", "multipleJobs_ctc.xml", tmpDir.resolve(MULTIPLE_JOBS_FILENAME));
    }
}

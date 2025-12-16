/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventReferenceVoltageVariationBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class ReferenceVoltageVariationEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                .staticId("GEN")
                .parameterSetId("GSFWPR")
                .build());
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPV")
                .staticId("GEN2")
                .parameterSetId("GPQ")
                .build());
        eventModels.add(EventReferenceVoltageVariationBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .deltaU(1.1)
                .build());
        eventModels.add(EventReferenceVoltageVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaU(1.1)
                .build());

    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "upv_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "upv_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

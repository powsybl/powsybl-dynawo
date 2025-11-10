/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventReactivePowerVariationBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Riad Benradi {@benradiria <riad.benradi at rte-france.com>}
 */
class ReactivePowerVariationEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD2")
                .parameterSetId("load")
                .build());
        eventModels.add(EventReactivePowerVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaQ(1.1)
                .build());
        eventModels.add(EventReactivePowerVariationBuilder.of(network)
                .staticId("LOAD")
                .startTime(10)
                .deltaQ(1.2)
                .build());
        eventModels.add(EventReactivePowerVariationBuilder.of(network)
                .staticId("LOAD2")
                .startTime(10)
                .deltaQ(1.3)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "rpv_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "rpv_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

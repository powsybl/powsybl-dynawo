/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.lines.LineBuilder;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DisconnectDynamicBusEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = SvcTestCaseFactory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .staticId("G1")
                .parameterSetId("gen")
                .build());
        dynamicModels.add(StandardBusBuilder.of(network)
                .staticId("B1")
                .parameterSetId("bus")
                .build());
        dynamicModels.add(LineBuilder.of(network)
                .staticId("L1")
                .parameterSetId("line")
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("B1")
                .startTime(1)
                .build());
    }

    @Test
    void testEmptyEvent() throws IOException, SAXException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_dynamic_bus_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_dynamic_bus_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

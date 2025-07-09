/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.shunts.BaseShuntBuilder;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DisconnectEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = SvcTestCaseFactory.create();
        VoltageLevel vl = network.getVoltageLevel("VL1");
        Bus b = vl.getBusBreakerView().getBus("B1");
        b.setV(400).setAngle(0);
        network.getBusBreakerView().getBus("B2").setV(400).setAngle(0);
        vl.newShuntCompensator()
                .setId("SH1")
                .setConnectableBus(b.getId())
                .setBus(b.getId())
                .setSectionCount(1)
                .newLinearModel()
                .setMaximumSectionCount(1)
                .setBPerSection(10)
                .add()
                .add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .staticId("G1")
                .parameterSetId("GF")
                .build());
        dynamicModels.add(BaseShuntBuilder.of(network)
                .staticId("SH1")
                .parameterSetId("BS")
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("G1")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("L2")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("SVC2")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("SH1")
                .startTime(1)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

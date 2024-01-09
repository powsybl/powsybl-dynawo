/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.builders.EventModelsBuilderUtils;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
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
        Generator g = network.getGenerator("G1");
        dynamicModels.add(new GeneratorFictitious("BBM_GEN", g, "GF", "GeneratorFictitious"));
        eventModels.add(EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId(g.getId())
                .startTime(1)
                .build());
        eventModels.add(EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("L2")
                .startTime(1)
                .build());
        eventModels.add(EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("SVC2")
                .startTime(1)
                .build());
        eventModels.add(EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("SH1")
                .startTime(1)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

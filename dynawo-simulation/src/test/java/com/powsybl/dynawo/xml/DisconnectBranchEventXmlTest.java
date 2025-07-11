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
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DisconnectBranchEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
    }

    @Override
    protected void addDynamicModels() {
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("NHV1_NHV2_1")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("NGEN_NHV1")
                .startTime(1)
                .disconnectOnly(TwoSides.ONE)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_branch_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_branch_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

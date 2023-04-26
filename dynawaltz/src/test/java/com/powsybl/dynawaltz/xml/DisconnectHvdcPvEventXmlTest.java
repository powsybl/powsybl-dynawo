/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.events.EventHvdcDisconnection;
import com.powsybl.dynawaltz.models.hvdc.HvdcPv;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

//TODO use parametrized tests
/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DisconnectHvdcPvEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new HvdcPv("BBM_HVDC", network.getHvdcLine("L"), "hvdc", "HvdcPV"));
        eventModels.add(new EventHvdcDisconnection(network.getHvdcLine("L"), 1, true, false));
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_hvdc_pv_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_hvdc_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

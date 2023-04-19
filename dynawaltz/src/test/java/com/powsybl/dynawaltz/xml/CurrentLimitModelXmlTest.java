/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class CurrentLimitModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new CurrentLimitAutomaton("BBM_CLA", "cla", network.getLine("NHV1_NHV2_1"), Side.ONE));
    }

    @Test
    void writeHvdcModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "cla_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.shunts.BaseShuntBuilder;
import com.powsybl.iidm.network.test.ShuntTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ShuntModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = ShuntTestCaseFactory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseShuntBuilder.of(network)
                .dynamicModelId("BBM_SHUNT")
                .staticId("SHUNT")
                .parameterSetId("sh")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "shunt_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

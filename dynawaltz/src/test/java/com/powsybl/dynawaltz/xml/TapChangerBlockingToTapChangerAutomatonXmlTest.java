/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class TapChangerBlockingToTapChangerAutomatonXmlTest extends TapChangerAutomatonXmlTest {

    @Override
    protected void addDynamicModels() {
        super.addDynamicModels();
        dynamicModels.add(new TapChangerBlockingAutomaton("BBM_TapChangerBlocking", "TapChangerPar",
                Collections.emptyList(),
                Collections.emptyList(),
                List.of("BBM_TC", "BBM_TC2", "BBM_TC3"),
                List.of(network.getBusBreakerView().getBus("NHV1")),
                "TapChangerBlockingAutomaton1"));
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_blocking_tap_changer_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

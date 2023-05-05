/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class TapChangerAutomatonXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
        VoltageLevel vlload = network.getVoltageLevel("VLLOAD");
        Bus nload = network.getBusBreakerView().getBus("NLOAD");
        vlload.newLoad().setId("LOAD2").setBus(nload.getId()).setConnectableBus(nload.getId()).setP0(600.0).setQ0(200.0).add();
        vlload.newLoad().setId("LOAD3").setBus(nload.getId()).setConnectableBus(nload.getId()).setP0(600.0).setQ0(200.0).add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new LoadOneTransformer("BBM_LOAD", network.getLoad("LOAD"), "LOT"));
        dynamicModels.add(new LoadTwoTransformers("BBM_LOAD2", network.getLoad("LOAD2"), "LTT"));
        dynamicModels.add(new LoadTwoTransformers("BBM_LOAD3", network.getLoad("LOAD3"), "LTT"));
        dynamicModels.add(new TapChangerAutomaton("BBM_TC", "tc", network.getLoad("LOAD")));
        dynamicModels.add(new TapChangerAutomaton("BBM_TC2", "tc", network.getLoad("LOAD2"), TransformerSide.LOW_VOLTAGE));
        dynamicModels.add(new TapChangerAutomaton("BBM_TC3", "tc", network.getLoad("LOAD3"), TransformerSide.HIGH_VOLTAGE));
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

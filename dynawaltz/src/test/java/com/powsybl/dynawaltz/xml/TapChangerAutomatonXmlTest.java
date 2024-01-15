/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
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
        dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("LOT")
                .build());
        dynamicModels.add(LoadTwoTransformersBuilder.of(network, "LoadTwoTransformers")
                .dynamicModelId("BBM_LOAD2")
                .staticId("LOAD2")
                .parameterSetId("LTT")
                .build());
        dynamicModels.add(LoadTwoTransformersBuilder.of(network, "LoadTwoTransformers")
                .dynamicModelId("BBM_LOAD3")
                .staticId("LOAD3")
                .parameterSetId("LTT")
                .build());
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId("LOAD")
                .build());
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC2")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .side(TransformerSide.LOW_VOLTAGE)
                .build());
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC3")
                .parameterSetId("tc")
                .staticId("LOAD3")
                .side(TransformerSide.HIGH_VOLTAGE)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChangerBuilder;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangersBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomatonBuilder;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class TapChangerBlockingAutomatonXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
        VoltageLevel vlload = network.getVoltageLevel("VLLOAD");
        Bus nload = network.getBusBreakerView().getBus("NLOAD");
        vlload.newLoad().setId("LOAD2").setBus(nload.getId()).setConnectableBus(nload.getId()).setP0(600.0).setQ0(200.0).add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(TransformerFixedRatioBuilder.of(network)
                .dynamicModelId("BBM_NGEN_NHV1")
                .staticId("NGEN_NHV1")
                .parameterSetId("transformer")
                .build());
        dynamicModels.add(LoadOneTransformerTapChangerBuilder.of(network, "LoadOneTransformerTapChanger")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("lot")
                .build());
        dynamicModels.add(LoadTwoTransformersTapChangersBuilder.of(network, "LoadTwoTransformersTapChangers")
                .dynamicModelId("BBM_LOAD2")
                .staticId("LOAD2")
                .parameterSetId("ltt")
                .build());
        dynamicModels.add(TapChangerBlockingAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TapChangerBlocking")
                .parameterSetId("TapChangerPar")
                .transformers("NGEN_NHV1", "NHV2_NLOAD", "LOAD", "LOAD2")
                .uMeasurements("NHV1", "NHV2")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_blocking_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    void testMonitoredEquipmentsLimit() {
        TapChangerBlockingAutomatonBuilder builder = TapChangerBlockingAutomatonBuilder.of(network)
                .dynamicModelId("TapChanger1")
                .parameterSetId("TapChangerPar")
                .transformers("NGEN_NHV1")
                .uMeasurements("NHV1", "NHV1", "NHV1", "NHV1", "NHV1", "NHV1");
        Exception e = assertThrows(PowsyblException.class, builder::build);
        assertEquals("Tap changer blocking automaton can only handle 5 measurement points at the same time", e.getMessage());
    }
}

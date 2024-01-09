/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.builders.DynamicModelBuilderUtils;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
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
        dynamicModels.add(new TransformerFixedRatio("BBM_NGEN_NHV1", network.getTwoWindingsTransformer("NGEN_NHV1"), "transformer", "TransformerFixedRatio"));
        dynamicModels.add(new LoadOneTransformerTapChanger("BBM_LOAD", network.getLoad("LOAD"), "lot", "LoadOneTransformerTapChanger"));
        dynamicModels.add(new LoadTwoTransformersTapChangers("BBM_LOAD2", network.getLoad("LOAD2"), "ltt", "LoadTwoTransformersTapChangers"));
        dynamicModels.add(DynamicModelBuilderUtils.newTapChangerBlockingAutomatonBuilder(network)
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
        Exception e = assertThrows(PowsyblException.class, () ->
                DynamicModelBuilderUtils.newTapChangerBlockingAutomatonBuilder(network)
                        .dynamicModelId("TapChanger1")
                        .parameterSetId("TapChangerPar")
                        .transformers("NGEN_NHV1")
                        .uMeasurements("NHV1", "NHV1", "NHV1", "NHV1", "NHV1", "NHV1")
                        .build());
        assertEquals("Tap changer blocking automaton can only handle 5 measurement points at the same time", e.getMessage());
    }
}

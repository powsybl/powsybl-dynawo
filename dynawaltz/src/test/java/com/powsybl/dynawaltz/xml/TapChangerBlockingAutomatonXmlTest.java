/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.TwoWindingsTransformer;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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
        dynamicModels.add(new TransformerFixedRatio("BBM_NGEN_NHV1", "NGEN_NHV1", "transformer", "TransformerFixedRatio"));
        dynamicModels.add(new LoadOneTransformerTapChanger("BBM_LOAD", "LOAD", "lot"));
        dynamicModels.add(new LoadTwoTransformersTapChangers("BBM_LOAD2", "LOAD2", "ltt"));
        dynamicModels.add(new TapChangerBlockingAutomaton("TapChanger1", "TapChangerPar",
                network.getTwoWindingsTransformerStream().collect(Collectors.toList()),
                List.of(network.getLoad("LOAD"), network.getLoad("LOAD2")),
                List.of(network.getBusBreakerView().getBus("NHV1"), network.getBusBreakerView().getBus("NHV2"))));
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    //TODO change test class ? + add test with load without transfo
    @Test
    void testMonitoredEquipmentsLimit() {

        List<Bus> buses = List.of(network.getBusBreakerView().getBus("NHV1"));
        List<TwoWindingsTransformer> emptyTransformerList = Collections.emptyList();
        List<TwoWindingsTransformer> transformers = new ArrayList<>();
        //TODO replace list with set ?
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));
        transformers.add(network.getTwoWindingsTransformer("NGEN_NHV1"));

        Exception e = assertThrows(PowsyblException.class, () ->
                new TapChangerBlockingAutomaton("TapChanger1",
                    "TapChangerPar",
                    emptyTransformerList,
                    buses));
        assertEquals("No Tap changers to monitor", e.getMessage());

        e = assertThrows(PowsyblException.class, () ->
                new TapChangerBlockingAutomaton("TapChanger1",
                        "TapChangerPar",
                        transformers,
                        buses));
        assertEquals("Tap changer blocking automaton can only handle 5 equipments at the same time", e.getMessage());
    }
}

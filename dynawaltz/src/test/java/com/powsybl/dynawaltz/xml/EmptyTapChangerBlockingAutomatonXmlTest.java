/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomatonBuilder;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerBuilder;
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
class EmptyTapChangerBlockingAutomatonXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
        VoltageLevel vlload = network.getVoltageLevel("VLLOAD");
        Bus nload = network.getBusBreakerView().getBus("NLOAD");
        vlload.newLoad().setId("LOAD2").setBus(nload.getId()).setConnectableBus(nload.getId()).setP0(600.0).setQ0(200.0).add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("lot")
                .build());
        dynamicModels.add(TapChangerBlockingAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TapChangerBlocking")
                .parameterSetId("TapChangerPar")
                .transformers("GEN", "LOAD", "BBM_TC")
                .uMeasurements("NHV1")
                .build());
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_blocking_empty_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        checkReporter("""
                + Test DYD
                  + Dynawaltz models processing
                     TapChangerAutomaton BBM_TC equipment is not a LoadWithTransformers, the automaton will be skipped
                     None of TapChangerBlockingAutomaton BBM_TapChangerBlocking equipments are TapChangerModel, the automaton will be skipped
                """);
    }
}

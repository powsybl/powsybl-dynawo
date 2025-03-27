/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EmptyTapChangerBlockingAutomationSystemXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
        network.getVoltageLevel("VLLOAD").newLoad()
                .setId("LOAD2")
                .setBus("NLOAD")
                .setConnectableBus("NLOAD")
                .setP0(600.0)
                .setQ0(200.0)
                .add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .staticId("LOAD")
                .parameterSetId("lot")
                .build());
        dynamicModels.add(TapChangerBlockingAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TapChangerBlocking")
                .parameterSetId("TapChangerPar")
                .transformers("GEN", "LOAD", "BBM_TC")
                .uMeasurements("NHV1")
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_blocking_empty_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkReport("""
                  + Test DYD
                     + Dynawo models processing
                        TapChangerAutomaton BBM_TC equipment LOAD2 is not a LoadWithTransformers, the automation system will be skipped
                        None of TapChangerBlockingAutomaton BBM_TapChangerBlocking equipments are TapChangerModel, the automation system will be skipped
                  """);
    }
}

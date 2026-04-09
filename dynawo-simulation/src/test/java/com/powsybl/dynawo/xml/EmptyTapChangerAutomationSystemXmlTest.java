/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EmptyTapChangerAutomationSystemXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId("LOAD")
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_HV")
                .parameterSetId("tc")
                .staticId("LOAD")
                .side(TransformerSide.HIGH_VOLTAGE)
                .build());
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_LV")
                .parameterSetId("tc")
                .staticId("LOAD")
                .side(TransformerSide.LOW_VOLTAGE)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_empty_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkConnected("BBM_TC", false);
        checkReport("""
                + Test DYD
                   + Dynawo models processing
                      TapChangerAutomationSystem BBM_TC requires a connection with a LoadWithTransformerModel but dynamic model LoadAlphaBeta LOAD does not implement it
                      TapChangerAutomationSystem BBM_TC connections cannot be created, the model will be skipped
                      TapChangerAutomationSystem BBM_TC_HV requires a connection with a LoadWithTransformersModel but dynamic model LoadAlphaBeta LOAD does not implement it
                      TapChangerAutomationSystem BBM_TC_HV connections cannot be created, the model will be skipped
                      TapChangerAutomationSystem BBM_TC_LV requires a connection with a LoadWithTransformersModel but dynamic model LoadAlphaBeta LOAD does not implement it
                      TapChangerAutomationSystem BBM_TC_LV connections cannot be created, the model will be skipped
                """);
    }
}

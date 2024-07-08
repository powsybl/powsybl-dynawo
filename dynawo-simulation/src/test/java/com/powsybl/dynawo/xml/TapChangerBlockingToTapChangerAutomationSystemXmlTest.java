/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class TapChangerBlockingToTapChangerAutomationSystemXmlTest extends TapChangerAutomationSystemXmlTest {

    @Override
    protected void addDynamicModels() {
        super.addDynamicModels();
        dynamicModels.add(TapChangerBlockingAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TapChangerBlocking")
                .parameterSetId("TapChangerPar")
                .transformers("BBM_TC", "BBM_TC2", "BBM_TC3")
                .uMeasurements("NHV1")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_blocking_tap_changer_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}

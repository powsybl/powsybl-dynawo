/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class UnderVoltageAutomationSystemXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMoreGenerators();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network, reportNode)
                .staticId("GEN")
                .parameterSetId("gen")
                .build());
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_under_voltage")
                .parameterSetId("uv")
                .generator("GEN")
                .build());
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_skipped_under_voltage")
                .parameterSetId("uv")
                .generator("GEN2")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "under_voltage_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkReport("""
                + Test DYD
                   Model GeneratorFictitious GEN instantiation OK
                   Model UnderVoltage BBM_under_voltage instantiation OK
                   Model UnderVoltage BBM_skipped_under_voltage instantiation OK
                   + Dynawo models processing
                      UnderVoltage BBM_skipped_under_voltage cannot handle connection with GENERATOR default model, the model will be skipped
                """);
    }
}

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
import com.powsybl.dynawo.models.generators.GridFormingConverterBuilder;
import com.powsybl.dynawo.models.generators.WeccBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.NGEN;
import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.VLGEN;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class UnderVoltageAutomationSystemXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
        addGenerator("GEN2");
        addGenerator("GEN3");
        addGenerator("GEN4");
    }

    private void addGenerator(String genId) {
        network.getVoltageLevel(VLGEN).newGenerator()
                .setId(genId)
                .setBus(NGEN)
                .setConnectableBus(NGEN)
                .setMinP(-9999.99)
                .setMaxP(9999.99)
                .setVoltageRegulatorOn(true)
                .setTargetV(24.5)
                .setTargetP(607.0)
                .setTargetQ(301.0)
                .add();
        network.getGenerator(genId).getTerminal()
                .setP(-605.558349609375)
                .setQ(-225.2825164794922);
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network, reportNode)
                .staticId("GEN")
                .parameterSetId("gen")
                .build());
        dynamicModels.add(WeccBuilder.of(network, "WT4AWeccCurrentSource")
                .staticId("GEN3")
                .build());
        dynamicModels.add(GridFormingConverterBuilder.of(network, "GridFormingConverterDroopControl")
                .staticId("GEN4")
                .parameterSetId("GF")
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
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_UVA_WECC")
                .parameterSetId("uv")
                .generator("GEN3")
                .build());
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_UVA_GF")
                .parameterSetId("uv")
                .generator("GEN4")
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
                   Model UnderVoltage BBM_UVA_WECC instantiation OK
                   Model UnderVoltage BBM_UVA_GF instantiation OK
                   + Dynawo models processing
                      UnderVoltageAutomaton BBM_skipped_under_voltage cannot handle connection with GENERATOR default model, the model will be skipped
                """);
    }
}

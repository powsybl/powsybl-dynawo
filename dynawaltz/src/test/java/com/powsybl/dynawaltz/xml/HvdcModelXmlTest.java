/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.hvdc.HvdcModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.VscConverterStation;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class HvdcModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    void createStaticModels() {
        VoltageLevel vlhv = network.getVoltageLevel("VLHV1");
        Bus nhv = vlhv.getBusBreakerView().getBus("NHV1");
        VscConverterStation cs1 = vlhv.newVscConverterStation()
                .setId("C1")
                .setName("Converter1")
                .setConnectableBus(nhv.getId())
                .setBus(nhv.getId())
                .setLossFactor(1.1f)
                .setVoltageSetpoint(405.0)
                .setVoltageRegulatorOn(true)
                .add();
        VoltageLevel vlgen = network.getVoltageLevel("VLHV2");
        Bus nhv2 = vlgen.getBusBreakerView().getBus("NHV2");
        VscConverterStation cs2 = vlgen.newVscConverterStation()
                .setId("C2")
                .setName("Converter2")
                .setConnectableBus(nhv2.getId())
                .setBus(nhv2.getId())
                .setLossFactor(1.1f)
                .setReactivePowerSetpoint(123)
                .setVoltageRegulatorOn(false)
                .setRegulatingTerminal(cs1.getTerminal())
                .add();
        network.newHvdcLine()
                .setId("HVDC_L")
                .setName("HVDC")
                .setConverterStationId1(cs1.getId())
                .setConverterStationId2(cs2.getId())
                .setR(1)
                .setNominalV(400)
                .setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER)
                .setMaxP(300.0)
                .setActivePowerSetpoint(280)
                .add();
    }

    @Override
    void createDynamicModels() {
        network.getHvdcLineStream().forEach(hvdc -> dynamicModels.add(new HvdcModel("BBM_" + hvdc.getId(), hvdc.getId(), "hv", "HvdcPV")));
    }

    @Test
    void writeHvdcModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "hvdc_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

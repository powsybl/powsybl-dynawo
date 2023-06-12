/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.hvdc.HvdcPv;
import com.powsybl.dynawaltz.models.hvdc.HvdcPvDangling;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class HvdcXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String HVDC_NAME = "L";
    private static final String DYN_NAME = "BBM_" + HVDC_NAME;

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> constructor) {
        setupNetwork();
        addDynamicModels(constructor);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideHvdc")
    void writeHvdcModel(String dydName, Function< Network, BlackBoxModel> constructor) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideHvdc() {
        return Stream.of(
                Arguments.of("hvdc_pv_dyd.xml", (Function<Network, BlackBoxModel>) n -> new HvdcPv(DYN_NAME, n.getHvdcLine(HVDC_NAME), "hv", "HvdcPV")),
                Arguments.of("hvdc_vsc_dyd.xml", (Function<Network, BlackBoxModel>) n -> new HvdcVsc(DYN_NAME, n.getHvdcLine(HVDC_NAME), "hv", "HvdcVSC")),
                Arguments.of("hvdc_pv_dangling_dyd.xml", (Function<Network, BlackBoxModel>) n -> new HvdcPvDangling(DYN_NAME, n.getHvdcLine(HVDC_NAME), "hv", "HvdcPVDangling", Side.ONE)),
                Arguments.of("hvdc_vsc_dangling_dyd.xml", (Function<Network, BlackBoxModel>) n -> new HvdcVscDangling(DYN_NAME, n.getHvdcLine(HVDC_NAME), "hv", "HvdcVSCDanglingP", Side.TWO))
        );
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.events.EventHvdcDisconnection;
import com.powsybl.dynawaltz.models.hvdc.HvdcPv;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
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
class DisconnectHvdcEventXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String HVDC_NAME = "L";
    private static final String DYN_HVDC_NAME = "BBM_HVDC";

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) {
        setupNetwork();
        addDynamicModels(hvdcConstructor, disconnectConstructor);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) {
        if (hvdcConstructor != null) {
            dynamicModels.add(hvdcConstructor.apply(network));
        }
        eventModels.add(disconnectConstructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void writeLoadModel(String dydName, Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_hvdc_par.xml", tmpDir.resolve(context.getSimulationParFile()));

    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("disconnect_default_hvdc_dyd.xml",
                        null,
                        (Function<Network, BlackBoxModel>) n -> new EventHvdcDisconnection(n.getHvdcLine(HVDC_NAME), 1)),
                Arguments.of("disconnect_hvdc_pv_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> new HvdcPv(DYN_HVDC_NAME, n.getHvdcLine(HVDC_NAME), "hvdc", "HvdcPV"),
                        (Function<Network, BlackBoxModel>) n -> new EventHvdcDisconnection(n.getHvdcLine(HVDC_NAME), 1, true, false)),
                Arguments.of("disconnect_hvdc_vsc_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> new HvdcVsc(DYN_HVDC_NAME, n.getHvdcLine(HVDC_NAME), "hvdc", "HvdcVsc"),
                        (Function<Network, BlackBoxModel>) n -> new EventHvdcDisconnection(n.getHvdcLine(HVDC_NAME), 1, false, true))

        );
    }
}
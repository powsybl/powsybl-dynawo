/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.generators.GridFormingConverter;
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen;
import com.powsybl.dynawaltz.models.generators.WeccGen;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
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
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class WeccGenXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String STATIC_ID = "GEN";
    private static final String DYN_WT_NAME = "BBM_WT";

    @BeforeEach
    void setup(String dydName, String parName, Function< Network, BlackBoxModel> loadConstructor) {
        setupNetwork();
        addDynamicModels(loadConstructor);
        setupDynawaltzContext();
    }

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideWecc")
    void writeModel(String dydName, String parName, Function< Network, BlackBoxModel> constructor) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        if (!parName.isEmpty()) {
            validate("parameters.xsd", parName, tmpDir.resolve(context.getSimulationParFile()));
        }
    }

    private static Stream<Arguments> provideWecc() {
        return Stream.of(
                Arguments.of("wecc_wt_dyd.xml", "", (Function<Network, BlackBoxModel>) n -> new WeccGen(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "WT4AWeccCurrentSource", "WT4A")),
                Arguments.of("wecc_wt_synchro_dyd.xml", "wecc_wt_par.xml", (Function<Network, BlackBoxModel>) n -> new SynchronizedWeccGen(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "WTG4BWeccCurrentSource", "WTG4B")),
                Arguments.of("wecc_pv_dyd.xml", "wecc_wt_par.xml", (Function<Network, BlackBoxModel>) n -> new SynchronizedWeccGen(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "PhotovoltaicsWeccCurrentSource", "photovoltaics")),
                Arguments.of("grid_forming_converter_dyd.xml", "grid_forming_converter_par.xml", (Function<Network, BlackBoxModel>) n -> new GridFormingConverter("BBM_GFC", n.getGenerator(STATIC_ID), "GF", "GridFormingConverterDroopControl"))
        );
    }
}

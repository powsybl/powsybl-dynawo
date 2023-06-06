/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.wecc.SynchronizedWecc;
import com.powsybl.dynawaltz.models.wecc.Wecc;
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
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class WtWeccXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String STATIC_ID = "GEN";
    private static final String DYN_WT_NAME = "BBM_WT";

    @BeforeEach
    void setup(String dydName, boolean isSynchronized, Function< Network, BlackBoxModel> loadConstructor) {
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
    @MethodSource("provideWtWecc")
    void writeModel(String dydName, boolean isSynchronized, Function< Network, BlackBoxModel> constructor) throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        if (isSynchronized) {
            validate("parameters.xsd", "wt_wecc_par.xml", tmpDir.resolve(context.getSimulationParFile()));
        }
    }

    private static Stream<Arguments> provideWtWecc() {
        return Stream.of(
                Arguments.of("wt_wecc_dyd.xml", false, (Function<Network, BlackBoxModel>) n -> new Wecc(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "WT4AWeccCurrentSource")),
                Arguments.of("wt_wecc_synchro_dyd.xml", true, (Function<Network, BlackBoxModel>) n -> new SynchronizedWecc(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "WTG4BWeccCurrentSource")),
                Arguments.of("wt_wecc_pv_dyd.xml", true, (Function<Network, BlackBoxModel>) n -> new SynchronizedWecc(DYN_WT_NAME, n.getGenerator(STATIC_ID), "Wind", "PhotovoltaicsWeccCurrentSource"))
        );
    }
}

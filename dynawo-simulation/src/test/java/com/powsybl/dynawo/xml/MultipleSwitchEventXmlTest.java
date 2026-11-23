/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventCloseSwitchBuilder;
import com.powsybl.dynawo.models.events.EventOpenSwitchBuilder;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MultipleSwitchEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = FourSubstationsNodeBreakerFactory.create();
    }

    @Override
    protected void addDynamicModels() {
        eventModels.add(EventOpenSwitchBuilder.of(network, reportNode)
                .staticId("S1VL1_LD1_BREAKER")
                .startTime(2)
                .build());
        eventModels.add(EventCloseSwitchBuilder.of(network, reportNode)
                .staticId("S1VL1_LD1_BREAKER")
                .startTime(4)
                .build());
        eventModels.add(EventOpenSwitchBuilder.of(network, reportNode)
                .staticId("S1VL1_LD1_BREAKER")
                .startTime(6)
                .build());
        eventModels.add(EventCloseSwitchBuilder.of(network, reportNode)
                .staticId("S1VL1_LD1_BREAKER")
                .startTime(8)
                .build());
    }

    @Test
    void writeMultipleSwitchEvents() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "multiple_switch_event_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "multiple_switch_event_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.iidm.network.extensions.StandbyAutomatonAdder;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
class SvarcModelWithSbaXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = SvcTestCaseFactory.create();
        network.getStaticVarCompensator("SVC2").newExtension(StandbyAutomatonAdder.class)
                .withB0(0.0001f)
                .withStandbyStatus(true)
                .withLowVoltageSetpoint(390f)
                .withHighVoltageSetpoint(400f)
                .withLowVoltageThreshold(385f)
                .withHighVoltageThreshold(405f)
                .add();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseStaticVarCompensatorBuilder.of(network)
                .staticId("SVC2")
                .parameterSetId("svc")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "svarc_sba_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}

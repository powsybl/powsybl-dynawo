/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class PhaseShifterBlockingIXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(PhaseShifterIAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_PS")
                .parameterSetId("ps")
                .transformer("NGEN_NHV1")
                .build());
        dynamicModels.add(PhaseShifterBlockingIAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_PSB")
                .parameterSetId("psb")
                .phaseShifterId("BBM_PS")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "phase_shifter_blocking_i_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}

/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.iidm.network.test.PhaseShifterTestCaseFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EmptyPhaseShifterBlockingIXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = PhaseShifterTestCaseFactory.create();
    }

    @Override
    protected void addDynamicModels() {
        addModelIfNotNull(
            PhaseShifterIAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_PS")
                .parameterSetId("ps")
                .transformer("PS1")
                .build(),
            PhaseShifterBlockingIAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_PSB")
                .parameterSetId("psb")
                .phaseShifterId("WRONG_ID")
                .build(),
            PhaseShifterBlockingIAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_PSB2")
                .parameterSetId("psb")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "empty_phase_shifter_blocking_i_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkReport("""
                + Test DYD
                   Model PhaseShifterI BBM_PS instantiation OK
                   Model PhaseShifterBlockingI BBM_PSB instantiation OK
                   + Model PhaseShifterBlockingI BBM_PSB2 instantiation KO
                      'phaseShifterId' field is not set
                   + Dynawo models processing
                      PhaseShifterBlockingI BBM_PSB equipment WRONG_ID is not a PhaseShifterIModel, the automation system will be skipped
                """);
    }
}

/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowRunParameters;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class UnsupportedParametersHandlerTest {

    @Test
    void testCheckParameters() {
        LoadFlowRunParameters runParameters = new LoadFlowRunParameters();
        runParameters.getLoadFlowParameters().setDc(false);
        DynaFlowProvider provider = new DynaFlowProvider();
        assertTrue(provider.checkParameters(runParameters));
    }

    @Test
    void testCheckAllUnsupportedParameters() throws IOException {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testParam")
                .build();
        LoadFlowRunParameters runParameters = new LoadFlowRunParameters();
        runParameters.setReportNode(reportNode);
        runParameters.getLoadFlowParameters()
                .setDc(true)
                .setWriteSlackBus(true)
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.DC_VALUES)
                .setTransformerVoltageControlOn(true)
                .setPhaseShifterRegulationOn(true)
                .setTwtSplitShuntAdmittance(true)
                .setCountriesToBalance(Set.of(Country.AE))
                .setHvdcAcEmulation(false)
                .setComponentMode(LoadFlowParameters.ComponentMode.MAIN_CONNECTED)
                .setDistributedSlack(false)
                .setBalanceType(LoadFlowParameters.BalanceType.PROPORTIONAL_TO_GENERATION_REMAINING_MARGIN);
        DynaFlowProvider provider = new DynaFlowProvider();

        String expectedReport = """
                + Test parameters
                   + Check load flow parameters compatibility with DynaFlow
                      Load flow parameter 'DC power flow' is not supported by DynaFlow, the load flow cannot be run
                      Load flow parameter 'WriteSlackBus' is not supported by DynaFlow, the parameter will be ignored
                      Load flow parameter 'VoltageInitMode' is not supported by DynaFlow, the parameter will be ignored
                      Load flow parameter 'TransformerVoltageControlOn' is not supported by DynaFlow, the IIDM property will be used instead
                      Load flow parameter 'PhaseShifterRegulationOn' is not supported by DynaFlow, the IIDM property will be used instead
                      Load flow parameter 'TwtSplitShuntAdmittance' is not supported by DynaFlow, the IIDM property will be used instead
                      Load flow parameter 'CountriesToBalance' is not supported by DynaFlow, the IIDM property will be used instead
                      Load flow parameter 'HvdcAcEmulation' is not supported by DynaFlow, the IIDM property will be used instead
                      Load flow parameter 'ComponentMode' value MAIN_CONNECTED is not supported by DynaFlow, the value MAIN_SYNCHRONOUS will be used instead
                      Load flow parameter 'DistributedSlack' value false is not supported by DynaFlow, the value true will be used instead
                      Load flow parameter 'BalanceType' value PROPORTIONAL_TO_GENERATION_REMAINING_MARGIN is not supported by DynaFlow, the value PROPORTIONAL_TO_GENERATION_P_MAX will be used instead
                """;

        assertFalse(provider.checkParameters(runParameters));
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }

    @Test
    void testCriticalParametersException() {
        Network network = EurostagTutorialExample1Factory.create();
        String variantId = network.getVariantManager().getWorkingVariantId();
        LoadFlowRunParameters runParameters = new LoadFlowRunParameters();
        runParameters.getLoadFlowParameters().setDc(true);
        DynaFlowProvider provider = new DynaFlowProvider();
        assertThatThrownBy(() -> provider.run(network, variantId, runParameters))
                .isInstanceOf(PowsyblException.class)
                .hasMessage("DC power flow is not implemented in DynaFlow");
    }
}

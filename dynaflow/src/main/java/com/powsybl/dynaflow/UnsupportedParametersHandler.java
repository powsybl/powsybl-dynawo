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
import com.powsybl.loadflow.LoadFlowParameters;

/**
 * Log or replace unsupported parameters
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class UnsupportedParametersHandler {

    private UnsupportedParametersHandler() {
    }

    public static boolean checkParameters(LoadFlowParameters loadFlowParameters, ReportNode reportNode) {

        boolean isCompatible = checkCriticalParameters(loadFlowParameters, false, reportNode);
        // Unsupported parameters
        if (loadFlowParameters.isWriteSlackBus()) {
            DynaflowReports.createIgnoredParameterReportNode(reportNode, "WriteSlackBus");
        }
        if (loadFlowParameters.getVoltageInitMode() != LoadFlowParameters.DEFAULT_VOLTAGE_INIT_MODE) {
            DynaflowReports.createIgnoredParameterReportNode(reportNode, "VoltageInitMode");
        }

        //IIDM properties
        if (loadFlowParameters.isTransformerVoltageControlOn() != LoadFlowParameters.DEFAULT_TRANSFORMER_VOLTAGE_CONTROL_ON) {
            DynaflowReports.createIidmReplacedParameterReportNode(reportNode, "TransformerVoltageControlOn");
        }
        if (loadFlowParameters.isPhaseShifterRegulationOn() != LoadFlowParameters.DEFAULT_PHASE_SHIFTER_REGULATION_ON) {
            DynaflowReports.createIidmReplacedParameterReportNode(reportNode, "PhaseShifterRegulationOn");
        }
        if (loadFlowParameters.isTwtSplitShuntAdmittance() != LoadFlowParameters.DEFAULT_TWT_SPLIT_SHUNT_ADMITTANCE) {
            DynaflowReports.createIidmReplacedParameterReportNode(reportNode, "TwtSplitShuntAdmittance");
        }
        if (!loadFlowParameters.getCountriesToBalance().isEmpty()) {
            DynaflowReports.createIidmReplacedParameterReportNode(reportNode, "CountriesToBalance");
        }
        if (loadFlowParameters.isHvdcAcEmulation() != LoadFlowParameters.DEFAULT_HVDC_AC_EMULATION_ON) {
            DynaflowReports.createIidmReplacedParameterReportNode(reportNode, "HvdcAcEmulation");
        }

        // Replaced values
        LoadFlowParameters.ComponentMode componentMode = loadFlowParameters.getComponentMode();
        if (componentMode != LoadFlowParameters.ComponentMode.MAIN_SYNCHRONOUS) {
            DynaflowReports.createReplacedParameterValueReportNode(reportNode, "ComponentMode",
                    componentMode.toString(), LoadFlowParameters.ComponentMode.MAIN_SYNCHRONOUS.toString());
        }
        boolean isDistributedSlack = loadFlowParameters.isDistributedSlack();
        if (!isDistributedSlack) {
            DynaflowReports.createReplacedParameterValueReportNode(reportNode, "DistributedSlack",
                    Boolean.toString(isDistributedSlack), Boolean.TRUE.toString());
        }
        LoadFlowParameters.BalanceType balanceType = loadFlowParameters.getBalanceType();
        if (isUnsupportedBalanceType(balanceType)) {
            DynaflowReports.createReplacedParameterValueReportNode(reportNode, "BalanceType",
                    balanceType.toString(), LoadFlowParameters.DEFAULT_BALANCE_TYPE.toString());
        }
        return isCompatible;
    }

    /**
     * Checks critical parameters and throw exception when an unsupported parameter is found
     */
    public static void checkCriticalParameters(LoadFlowParameters loadFlowParameters) {
        checkCriticalParameters(loadFlowParameters, true, ReportNode.NO_OP);
    }

    private static boolean checkCriticalParameters(LoadFlowParameters loadFlowParameters, boolean throwException, ReportNode reportNode) {
        if (loadFlowParameters.isDc()) {
            if (throwException) {
                throw new PowsyblException("DC power flow is not implemented in DynaFlow");
            }
            DynaflowReports.createCriticalUnsupportedParameterReportNode(reportNode, "DC power flow");
            return false;
        }
        return true;
    }

    private static boolean isUnsupportedBalanceType(LoadFlowParameters.BalanceType balanceType) {
        return switch (balanceType) {
            case PROPORTIONAL_TO_GENERATION_PARTICIPATION_FACTOR, PROPORTIONAL_TO_GENERATION_REMAINING_MARGIN, PROPORTIONAL_TO_CONFORM_LOAD -> true;
            default -> false;
        };
    }
}

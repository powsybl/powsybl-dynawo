/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.loadflow.LoadFlowParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log or replace unsupported parameters
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class UnsupportedParametersHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsupportedParametersHandler.class);

    private UnsupportedParametersHandler() {
    }

    public static void checkParameters(LoadFlowParameters loadFlowParameters) {
        // Unsupported parameters
        if (loadFlowParameters.isDc()) {
            LOGGER.error("DC power flow is not implemented in DynaFlow, the parameter will be ignored");
        }
        if (loadFlowParameters.isWriteSlackBus()) {
            LOGGER.warn("Load flow parameter WriteSlackBus is not implemented in DynaFlow, the parameter will be ignored");
        }
        if (loadFlowParameters.getVoltageInitMode() != LoadFlowParameters.DEFAULT_VOLTAGE_INIT_MODE) {
            LOGGER.warn("Load flow parameter VoltageInitMode is not implemented in DynaFlow, the parameter will be ignored");
        }

        //IIDM properties
        if (loadFlowParameters.isTransformerVoltageControlOn() != LoadFlowParameters.DEFAULT_TRANSFORMER_VOLTAGE_CONTROL_ON) {
            logIidmProperty("TransformerVoltageControlOn");
        }
        if (loadFlowParameters.isPhaseShifterRegulationOn() != LoadFlowParameters.DEFAULT_PHASE_SHIFTER_REGULATION_ON) {
            logIidmProperty("PhaseShifterRegulationOn");
        }
        if (loadFlowParameters.isTwtSplitShuntAdmittance() != LoadFlowParameters.DEFAULT_TWT_SPLIT_SHUNT_ADMITTANCE) {
            logIidmProperty("TwtSplitShuntAdmittance");
        }
        if (!loadFlowParameters.getCountriesToBalance().isEmpty()) {
            logIidmProperty("CountriesToBalance");
        }
        if (loadFlowParameters.isHvdcAcEmulation() != LoadFlowParameters.DEFAULT_HVDC_AC_EMULATION_ON) {
            logIidmProperty("HvdcAcEmulation");
        }

        // Replaced values
        LoadFlowParameters.ComponentMode componentMode = loadFlowParameters.getComponentMode();
        if (componentMode != LoadFlowParameters.ComponentMode.MAIN_SYNCHRONOUS) {
            logUnsupportedValue("ComponentMode", componentMode.toString(), LoadFlowParameters.ComponentMode.MAIN_SYNCHRONOUS.toString());
        }
        boolean isDistributedSlack = loadFlowParameters.isDistributedSlack();
        if (!isDistributedSlack) {
            logUnsupportedValue("DistributedSlack", Boolean.toString(isDistributedSlack), Boolean.TRUE.toString());
        }
        LoadFlowParameters.BalanceType balanceType = loadFlowParameters.getBalanceType();
        if (isUnsupportedBalanceType(balanceType)) {
            logUnsupportedValue("BalanceType", balanceType.toString(), LoadFlowParameters.DEFAULT_BALANCE_TYPE.toString());
            loadFlowParameters.setBalanceType(LoadFlowParameters.DEFAULT_BALANCE_TYPE);
        }
    }

    private static void logIidmProperty(String parameter) {
        LOGGER.warn("Load flow parameter {} is not supported, DynaFlow will use the IIDM property instead", parameter);
    }

    private static void logUnsupportedValue(String parameter, String value, String replacingValue) {
        LOGGER.warn("Load flow parameter {} value {} is not supported, the value {} will be used instead", parameter, value, replacingValue);
    }

    private static boolean isUnsupportedBalanceType(LoadFlowParameters.BalanceType balanceType) {
        return switch (balanceType) {
            case PROPORTIONAL_TO_GENERATION_PARTICIPATION_FACTOR, PROPORTIONAL_TO_LOAD, PROPORTIONAL_TO_CONFORM_LOAD -> true;
            default -> false;
        };
    }
}

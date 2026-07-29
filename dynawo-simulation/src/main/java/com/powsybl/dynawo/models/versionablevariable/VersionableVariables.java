/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import com.google.auto.service.AutoService;
import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(VersionableVariablesProvider.class)
public final class VersionableVariables implements VersionableVariablesProvider {

    private static final DynawoVersion DYNAWO_VERSION_1_8_0 = new DynawoVersion(1, 8, 0);

    private static final VersionableVariable STEP = new VersionableVariable("STEP",
            new VersionableVariable.VariableStep("step_step_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "step_step"));

    private static final VersionableVariable OMEGA_GRP = new VersionableVariable("OMEGA_GRP",
            new VersionableVariable.VariableStep("omega_grp_@INDEX@"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "omega_grp_@INDEX@_value"));

    private static final VersionableVariable OMEGA_REF_GRP = new VersionableVariable("OMEGA_REF_GRP",
            new VersionableVariable.VariableStep("omegaRef_grp_@INDEX@"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "omegaRef_grp_@INDEX@_value"));

    private static final VersionableVariable TC_LOCKED = new VersionableVariable("TC_LOCKED",
            new VersionableVariable.VariableStep("tapChanger%s_locked"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_locked"));

    private static final VersionableVariable TCB_LOCKED = new VersionableVariable("TCB_LOCKED",
            new VersionableVariable.VariableStep("%s_TAP_CHANGER_locked_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s_TAP_CHANGER_locked"));

    private static final VersionableVariable TC_SWITCH_OFF = new VersionableVariable("TC_SWITCH_OFF",
            new VersionableVariable.VariableStep("tapChanger%s_switchOffSignal1"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_tapChanger_switchOffSignal1"));

    private static final VersionableVariable DELTA_PC = new VersionableVariable("DELTA_PC",
            new VersionableVariable.VariableStep("@NAME@_DeltaPc_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "@NAME@_DeltaPc"));

    private static final VersionableVariable DELTA_QC = new VersionableVariable("DELTA_QC",
            new VersionableVariable.VariableStep("@NAME@_DeltaQc_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "@NAME@_DeltaQc"));

    private static final VersionableVariable DELTA_P_LOAD = new VersionableVariable("DELTA_P_LOAD",
            new VersionableVariable.VariableStep("DeltaPc_load_@INDEX@_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "DeltaPc_load_@INDEX@"));

    private static final VersionableVariable DELTA_Q_LOAD = new VersionableVariable("DELTA_Q_LOAD",
            new VersionableVariable.VariableStep("DeltaQc_load_@INDEX@_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "DeltaQc_load_@INDEX@"));

    private static final VersionableVariable DELTA_P_GEN = new VersionableVariable("DELTA_P_GEN",
            new VersionableVariable.VariableStep("generator_deltaPmRefPu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "generator_deltaPmRefPu"));

    private static final VersionableVariable EVENT_STATE = new VersionableVariable("EVENT_STATE",
            new VersionableVariable.VariableStep("event_state1_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "event_state1"));

    private static final VersionableVariable LOAD_OMEGA_REF_PU = new VersionableVariable("LOAD_OMEGA_REF_PU",
            new VersionableVariable.VariableStep("load_omegaRefPu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "load_omegaRefPu"));

    private static final VersionableVariable PHI = new VersionableVariable("PHI",
            new VersionableVariable.VariableStep("@@NAME@@@NODE@_phi_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "@@NAME@@@NODE@_phi"));

    private static final VersionableVariable STATE = new VersionableVariable("STATE",
            new VersionableVariable.VariableStep("%s_state_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s_state_value"));

    private static final VersionableVariable SIDED_STATE = new VersionableVariable("SIDED_STATE",
            new VersionableVariable.VariableStep("%s_state%s_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s_state%s_value"));

    private static final VersionableVariable SIDED_UPU = new VersionableVariable("SIDED_UPU",
            new VersionableVariable.VariableStep("%s@_Upu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s@_Upu"));

    private static final VersionableVariable SVARC_MODE_HANDLING = new VersionableVariable("SVARC_MODE_HANDLING",
            new VersionableVariable.VariableStep("SVarC_modeHandling_mode_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "SVarC_modeHandling_mode"));

    private static final VersionableVariable TRANSFORMER_P1PU = new VersionableVariable("TRANSFORMER_P1PU",
            new VersionableVariable.VariableStep("transformer%s_P1Pu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_P1Pu"));

    private static final VersionableVariable TRANSFORMER_Q1PU = new VersionableVariable("TRANSFORMER_Q1PU",
            new VersionableVariable.VariableStep("transformer%s_Q1Pu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_Q1Pu"));

    private static final VersionableVariable TRANSFORMER_U2PU = new VersionableVariable("TRANSFORMER_U2PU",
            new VersionableVariable.VariableStep("transformer_U2Pu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer_U2Pu"));

    private static final List<VersionableVariable> VERSIONABLE_VARIABLES = List.of(STEP, OMEGA_GRP, OMEGA_REF_GRP,
            TC_LOCKED, TCB_LOCKED, TC_SWITCH_OFF, DELTA_PC, DELTA_QC, DELTA_P_LOAD, DELTA_Q_LOAD, DELTA_P_GEN,
            EVENT_STATE, LOAD_OMEGA_REF_PU, PHI, STATE, SIDED_STATE, SIDED_UPU, SVARC_MODE_HANDLING, TRANSFORMER_P1PU,
            TRANSFORMER_Q1PU, TRANSFORMER_U2PU);

    @Override
    public List<VersionableVariable> getVersionableVariables() {
        return VERSIONABLE_VARIABLES;
    }
}

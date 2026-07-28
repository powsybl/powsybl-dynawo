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

import java.util.Map;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(VersionableVariablesResolver.class)
public final class VersionableVariables implements VersionableVariablesResolver {

    private static final DynawoVersion DYNAWO_VERSION_1_8_0 = new DynawoVersion(1, 8, 0);

    private static final VersionableVariable STEP = new VersionableVariable(
            new VersionableVariable.VariableStep("step_step_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "step_step"));

    private static final VersionableVariable OMEGA_GRP = new VersionableVariable(
            new VersionableVariable.VariableStep("omega_grp_@INDEX@"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "omega_grp_@INDEX@_value"));

    private static final VersionableVariable OMEGA_REF_GRP = new VersionableVariable(
            new VersionableVariable.VariableStep("omegaRef_grp_@INDEX@"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "omegaRef_grp_@INDEX@_value"));

    private static final VersionableVariable TC_LOCKED = new VersionableVariable(
            new VersionableVariable.VariableStep("tapChanger%s_locked"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_locked"));

    private static final VersionableVariable TC_SWITCH_OFF = new VersionableVariable(
            new VersionableVariable.VariableStep("tapChanger%s_switchOffSignal1"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_tapChanger_switchOffSignal1"));

    private static final VersionableVariable DELTA_PC = new VersionableVariable(
            new VersionableVariable.VariableStep("@NAME@_DeltaPc_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "@NAME@_DeltaPc"));

    private static final VersionableVariable DELTA_QC = new VersionableVariable(
            new VersionableVariable.VariableStep("@NAME@_DeltaQc_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "@NAME@_DeltaQc"));

    private static final VersionableVariable DELTA_P_LOAD = new VersionableVariable(
            new VersionableVariable.VariableStep("DeltaPc_load_@INDEX@_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "DeltaPc_load_@INDEX@"));

    private static final VersionableVariable DELTA_Q_LOAD = new VersionableVariable(
            new VersionableVariable.VariableStep("DeltaQc_load_@INDEX@_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "DeltaQc_load_@INDEX@"));

    private static final VersionableVariable DELTA_P_GEN = new VersionableVariable(
            new VersionableVariable.VariableStep("generator_deltaPmRefPu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "generator_deltaPmRefPu"));

    private static final VersionableVariable EVENT_STATE = new VersionableVariable(
            new VersionableVariable.VariableStep("event_state1_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "event_state1"));

    private static final VersionableVariable LOAD_OMEGA_REF_PU = new VersionableVariable(
            new VersionableVariable.VariableStep("load_omegaRefPu_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "load_omegaRefPu"));

    private static final VersionableVariable SVARC_MODE_HANDLING = new VersionableVariable(
            new VersionableVariable.VariableStep("SVarC_modeHandling_mode_value"),
            new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "SVarC_modeHandling_mode"));

    private static final Map<String, VersionableVariable> VERSIONABLE_VARIABLES_MAP = Map.ofEntries(
            Map.entry("DELTA_PC", DELTA_PC),
            Map.entry("DELTA_QC", DELTA_QC),
            Map.entry("DELTA_P_LOAD", DELTA_P_LOAD),
            Map.entry("DELTA_Q_LOAD", DELTA_Q_LOAD),
            Map.entry("DELTA_P_GEN", DELTA_P_GEN),
            Map.entry("EVENT_STATE", EVENT_STATE),
            Map.entry("LOAD_OMEGA_REF_PU", LOAD_OMEGA_REF_PU),
            Map.entry("STEP", STEP),
            Map.entry("SVARC_MODE_HANDLING", SVARC_MODE_HANDLING),
            Map.entry("OMEGA_GRP", OMEGA_GRP),
            Map.entry("OMEGA_REF_GRP", OMEGA_REF_GRP),
            Map.entry("TC_LOCKED", TC_LOCKED),
            Map.entry("TC_SWITCH_OFF", TC_SWITCH_OFF));

    public static String getCurrentValue(String name) {
        return VERSIONABLE_VARIABLES_MAP.get(name).getCurrentValue();
    }

    @Override
    public void setCurrentValues(DynawoVersion currentVersion) {
        VERSIONABLE_VARIABLES_MAP.values().forEach(vv -> vv.setCurrentValue(currentVersion));
    }
}

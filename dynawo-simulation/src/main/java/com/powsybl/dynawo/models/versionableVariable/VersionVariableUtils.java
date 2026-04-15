/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionableVariable;

import com.powsybl.dynawo.commons.DynawoVersion;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class VersionVariableUtils {

    public static final VersionableVariable STEP = new VersionableVariable("step_step_value",
            new VersionableVariable.VariableStep(new DynawoVersion(1, 8, 0), "step_step"));

    public static final VersionableVariable OMEGA_GRP = new VersionableVariable("omega_grp_@INDEX@",
            new VersionableVariable.VariableStep(new DynawoVersion(1, 8, 0), "omega_grp_@INDEX@_value"));

    public static final VersionableVariable OMEGA_REF_GRP = new VersionableVariable("omegaRef_grp_@INDEX@",
            new VersionableVariable.VariableStep(new DynawoVersion(1, 8, 0), "omegaRef_grp_@INDEX@_value"));

    public static final VersionableVariable TC_LOCKED = new VersionableVariable("tapChanger%s_locked",
            new VersionableVariable.VariableStep(new DynawoVersion(1, 8, 0), "transformer%s_locked"));

    public static final VersionableVariable TC_SWITCH_OFF = new VersionableVariable("tapChanger%s_switchOffSignal1",
            new VersionableVariable.VariableStep(new DynawoVersion(1, 8, 0), "transformer%s_tapChanger_switchOffSignal1"));

    private VersionVariableUtils() {
    }

}

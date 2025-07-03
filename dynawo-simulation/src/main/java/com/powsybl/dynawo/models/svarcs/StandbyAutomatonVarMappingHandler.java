/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.svarcs;

import com.powsybl.dynawo.models.VarMapping;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandbyAutomatonVarMappingHandler implements SvarcVarMappingHandler {

    private static final List<VarMapping> VAR_MAPPING = List.of(P_MAPPING, Q_MAPPING, STATE_MAPPING,
            new VarMapping("SVarC_modeHandling_mode_value", "regulatingMode"));

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }
}

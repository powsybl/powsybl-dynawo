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
public interface SvarcVarMappingHandler {

    VarMapping P_MAPPING = new VarMapping("SVarC_injector_PInjPu", "p");
    VarMapping Q_MAPPING = new VarMapping("SVarC_injector_QInjPu", "q");
    VarMapping STATE_MAPPING = new VarMapping("SVarC_injector_state", "state");

    List<VarMapping> getVarsMapping();
}

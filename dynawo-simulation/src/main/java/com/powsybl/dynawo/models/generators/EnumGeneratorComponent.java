/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.models.VarMapping;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum EnumGeneratorComponent {

    NONE("generator_terminal", List.of(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"))),
    TRANSFORMER("transformer_terminal1", List.of(
            new VarMapping("transformer_P1GenPu", "p"),
            new VarMapping("transformer_Q1GenPu", "q"),
            new VarMapping("generator_state", "state")
    )),
    AUXILIARY_TRANSFORMER("coupling_terminal1", List.of(
            new VarMapping("coupling_P1GenPu", "p"),
            new VarMapping("coupling_Q1GenPu", "q"),
            new VarMapping("generator_state", "state")
    ));

    EnumGeneratorComponent(String terminalVarName, List<VarMapping> varMapping) {
        this.terminalVarName = terminalVarName;
        this.varMapping = varMapping;
    }

    private final String terminalVarName;
    private final List<VarMapping> varMapping;

    public String getTerminalVarName() {
        return terminalVarName;
    }

    public List<VarMapping> getVarMapping() {
        return varMapping;
    }
}

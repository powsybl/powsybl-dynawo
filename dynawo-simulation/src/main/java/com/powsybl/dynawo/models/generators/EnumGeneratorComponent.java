/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum EnumGeneratorComponent {

    NONE("generator_terminal"),
    TRANSFORMER("transformer_terminal1"),
    AUXILIARY_TRANSFORMER("coupling_terminal1");

    EnumGeneratorComponent(String terminalVarName) {
        this.terminalVarName = terminalVarName;
    }

    private final String terminalVarName;

    public String getTerminalVarName() {
        return terminalVarName;
    }
}

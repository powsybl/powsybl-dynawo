/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;

import java.util.List;

import static com.powsybl.dynawo.models.generators.GeneratorProperties.GENERATOR_STATE;
import static com.powsybl.dynawo.models.generators.GeneratorProperties.STATE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum EnumGeneratorComponent {

    NONE("generator_terminal", List.of(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping(GENERATOR_STATE, STATE))),
    TRANSFORMER("transformer_terminal1", List.of(
            new VarMapping("transformer_P1GenPu", "p"),
            new VarMapping("transformer_Q1GenPu", "q"),
            new VarMapping(GENERATOR_STATE, STATE)
    )),
    AUXILIARY_TRANSFORMER("coupling_terminal1", List.of(
            new VarMapping("coupling_P1GenPu", "p"),
            new VarMapping("coupling_Q1GenPu", "q"),
            new VarMapping(GENERATOR_STATE, STATE)
    ));

    public static EnumGeneratorComponent createFrom(ModelConfig modelConfig) {
        boolean aux = modelConfig.hasAuxiliary();
        boolean transformer = modelConfig.hasTransformer();
        if (aux && transformer) {
            return EnumGeneratorComponent.AUXILIARY_TRANSFORMER;
        } else if (transformer) {
            return EnumGeneratorComponent.TRANSFORMER;
        } else if (aux) {
            throw new PowsyblException("Generator component auxiliary without transformer is not supported");
        }
        return EnumGeneratorComponent.NONE;
    }

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

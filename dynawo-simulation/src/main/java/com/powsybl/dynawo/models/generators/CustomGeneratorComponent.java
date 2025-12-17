/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;

import java.util.List;
import java.util.Map;

import static com.powsybl.dynawo.models.generators.GeneratorProperties.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record CustomGeneratorComponent(String terminal, String switchOffSignal, String omegaRefPu, String omegaPu,
                                       String running, List<VarMapping> varMapping) implements ComponentDescription {

    public static CustomGeneratorComponent fromModelConfig(ModelConfig modelConfig) {
        return fromModelConfig(modelConfig, EnumGeneratorComponent.NONE);
    }

    public static CustomGeneratorComponent fromModelConfig(ModelConfig modelConfig, EnumGeneratorComponent generatorComponent) {

        Map<String, String> variableMap = modelConfig.varPrefix();
        String terminal = variableMap.getOrDefault("terminal", generatorComponent.getTerminalVarName());
        String switchOffSignal = variableMap.getOrDefault("switchOffSignal", DEFAULT_SWITCH_OFF_SIGNAL);
        String omegaRefPu = variableMap.getOrDefault("omegaRefPu", DEFAULT_OMEGA_REF_PU);
        String omegaPu = variableMap.getOrDefault("omegaPu", DEFAULT_OMEGA_PU);
        String running = variableMap.getOrDefault("running", DEFAULT_RUNNING);

        List<VarMapping> varMapping = modelConfig.varMapping().isEmpty() ? generatorComponent.getVarMapping() : modelConfig.varMapping();
        return new CustomGeneratorComponent(terminal, switchOffSignal, omegaRefPu, omegaPu, running, varMapping);
    }

    public String switchOffSignalNode() {
        return switchOffSignal + 1;
    }

    public String switchOffSignalEvent() {
        return switchOffSignal + 2;
    }

    public String switchOffSignalAutomaton() {
        return switchOffSignal + 3;
    }
}

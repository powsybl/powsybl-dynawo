/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.generators.GeneratorModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class UnderVoltageAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    protected final Generator generator;

    protected UnderVoltageAutomationSystem(String dynamicModelId, String parameterSetId, Generator generator, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, generator, GeneratorModel.class, this::getVarConnectionsWith);
    }

    protected List<VarConnection> getVarConnectionsWith(GeneratorModel connected) {
        return Arrays.asList(
                new VarConnection("underVoltageAutomaton_UMonitoredPu", connected.getUPuVarName()),
                new VarConnection("underVoltageAutomaton_switchOffSignal", connected.getSwitchOffSignalAutomatonVarName())
        );
    }
}

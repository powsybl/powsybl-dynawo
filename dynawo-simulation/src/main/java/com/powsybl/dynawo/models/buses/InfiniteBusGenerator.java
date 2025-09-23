/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;

/**
 * Infinite bus dynamic model on generator
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class InfiniteBusGenerator extends AbstractEquipmentBlackBoxModel<Generator> {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("infiniteBus_OutputZero", "p"),
            new VarMapping("infiniteBus_OutputZero", "q"),
            new VarMapping("infiniteBus_state", "state"));

    protected InfiniteBusGenerator(Generator equipment, String parameterSetId, ModelConfig modelConfig) {
        super(equipment, parameterSetId, modelConfig);
    }

    @Override
    public String getName() {
        return getLib() + "Generator";
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("infiniteBus_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }
}

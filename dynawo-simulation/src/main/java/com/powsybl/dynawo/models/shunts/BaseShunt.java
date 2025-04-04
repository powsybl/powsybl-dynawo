/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.shunts;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.ShuntCompensator;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseShunt extends AbstractEquipmentBlackBoxModel<ShuntCompensator> implements ShuntModel {

    protected BaseShunt(ShuntCompensator svarc, String parameterSetId, ModelConfig modelConfig) {
        super(svarc, parameterSetId, modelConfig);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("shunt_terminal", connected.getTerminalVarName()));
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "shunt_switchOffSignal2";
    }

    @Override
    public String getStateVarName() {
        return "shunt_state";
    }

    @Override
    public String getIsCapacitorVarName() {
        return "shunt_isCapacitor";
    }

    @Override
    public String getIsAvailableVarName() {
        return "shunt_isAvailable";
    }
}

/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.switches.SwitchModel;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Switch;

import java.util.List;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;
import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventSwitchOpen extends AbstractEvent {

    private final boolean open;

    protected EventSwitchOpen(String eventId, Switch equipment, EventModelInfo eventModelInfo, double startTime, boolean open) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.open = open;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), SwitchModel.class, this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(SwitchModel connected) {
        return List.of(new VarConnection("event_state1_value", connected.getStateValueVarName()));
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("event_open", BOOL, Boolean.toString(open));
    }
}

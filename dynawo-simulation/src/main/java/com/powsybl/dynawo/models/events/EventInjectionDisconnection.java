/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.InjectionModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.iidm.network.*;

import java.util.List;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventInjectionDisconnection extends AbstractDynamicLibEventDisconnection {

    protected EventInjectionDisconnection(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, boolean disconnect) {
        super(eventId, equipment, eventModelInfo, startTime, disconnect);
    }

    private List<VarConnection> getVarConnectionsWith(InjectionModel connected) {
        return List.of(new VarConnection(equipmentModelType.getValue().getVarConnection(), connected.getSwitchOffSignalEventVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), InjectionModel.class, this::getVarConnectionsWith);
    }
}

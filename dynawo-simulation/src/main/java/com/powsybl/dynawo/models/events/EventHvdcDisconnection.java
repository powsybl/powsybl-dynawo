/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.hvdc.HvdcModel;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventHvdcDisconnection extends AbstractDynamicLibEventDisconnection {

    private final TwoSides disconnectSide;

    protected EventHvdcDisconnection(String eventId, HvdcLine equipment, EventModelInfo eventModelInfo, double startTime, TwoSides disconnectSide) {
        super(eventId, equipment, eventModelInfo, startTime, true);
        this.disconnectSide = disconnectSide;
    }

    private List<VarConnection> getVarConnectionsWithHvdcModel(HvdcModel connected) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(TwoSides.ONE)),
                new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(TwoSides.TWO)));
    }

    private List<VarConnection> getVarConnectionsWithHvdcModelSide(HvdcModel connected, TwoSides side) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(side)));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (disconnectSide == null) {
            adder.createMacroConnections(this, getEquipment(), HvdcModel.class, this::getVarConnectionsWithHvdcModel);
        } else {
            adder.createMacroConnections(this, getEquipment(), HvdcModel.class, this::getVarConnectionsWithHvdcModelSide, disconnectSide);
        }
    }
}

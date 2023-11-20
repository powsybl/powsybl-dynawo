/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.hvdc.HvdcModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.HvdcLine;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventHvdcDisconnection extends AbstractDynamicLibEventDisconnection {

    private final boolean disconnectOrigin;
    private final boolean disconnectExtremity;

    public EventHvdcDisconnection(HvdcLine equipment, double startTime, boolean disconnectOrigin, boolean disconnectExtremity) {
        super(equipment, startTime, disconnectOrigin || disconnectExtremity);
        this.disconnectOrigin = disconnectOrigin;
        this.disconnectExtremity = disconnectExtremity;
    }

    public EventHvdcDisconnection(HvdcLine equipment, double startTime) {
        this(equipment, startTime, true, true);
    }

    private List<VarConnection> getVarConnectionsWithHvdcModel(HvdcModel connected) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(Side.ONE)),
                new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(Side.TWO)));
    }

    private List<VarConnection> getVarConnectionsWithHvdcModelSide(HvdcModel connected, Side side) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName(side)));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (disconnectOrigin && disconnectExtremity) {
            adder.createMacroConnections(this, getEquipment(), HvdcModel.class, this::getVarConnectionsWithHvdcModel);
        } else if (disconnectOrigin) {
            adder.createMacroConnections(this, getEquipment(), HvdcModel.class, this::getVarConnectionsWithHvdcModelSide, Side.ONE);
        } else if (disconnectExtremity) {
            adder.createMacroConnections(this, getEquipment(), HvdcModel.class, this::getVarConnectionsWithHvdcModelSide, Side.TWO);
        }
    }
}

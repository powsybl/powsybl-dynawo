/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.InjectionModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.ShuntCompensator;
import com.powsybl.iidm.network.StaticVarCompensator;

import java.util.List;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventInjectionDisconnection extends AbstractDynamicLibEventDisconnection {

    public EventInjectionDisconnection(Generator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, disconnect);
    }

    public EventInjectionDisconnection(Generator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    public EventInjectionDisconnection(Load equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, disconnect);
    }

    public EventInjectionDisconnection(Load equipment, double startTime) {
        this(equipment, startTime, true);
    }

    public EventInjectionDisconnection(StaticVarCompensator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, disconnect);
    }

    public EventInjectionDisconnection(StaticVarCompensator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    public EventInjectionDisconnection(ShuntCompensator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, disconnect);
    }

    public EventInjectionDisconnection(ShuntCompensator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    private List<VarConnection> getVarConnectionsWith(InjectionModel connected) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getSwitchOffSignalEventVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), InjectionModel.class, this::getVarConnectionsWith);
    }
}

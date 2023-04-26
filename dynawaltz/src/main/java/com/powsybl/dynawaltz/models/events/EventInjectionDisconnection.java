/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventInjectionDisconnection extends AbstractDynamicLibEventDisconnection {

    private final boolean disconnect;

    public EventInjectionDisconnection(Generator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, EVENT_PREFIX);
        this.disconnect = disconnect;
    }

    public EventInjectionDisconnection(Generator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    public EventInjectionDisconnection(Load equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, EVENT_PREFIX);
        this.disconnect = disconnect;
    }

    public EventInjectionDisconnection(Load equipment, double startTime) {
        this(equipment, startTime, true);
    }

    private List<VarConnection> getVarConnectionsWithDisconnectable(DisconnectableEquipment connected) {
        return List.of(new VarConnection(DISCONNECTION_VAR_CONNECT, connected.getDisconnectableVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), DisconnectableEquipment.class, this::getVarConnectionsWithDisconnectable, context);
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "event_tEvent", Double.toString(getStartTime()));
        ParametersXml.writeParameter(writer, ParameterType.BOOL, "event_stateEvent1", Boolean.toString(disconnect));
    }
}

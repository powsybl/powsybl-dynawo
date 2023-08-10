/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Branch;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventQuadripoleDisconnection extends AbstractEventModel {

    private final boolean disconnectOrigin;
    private final boolean disconnectExtremity;

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime, boolean disconnectOrigin, boolean disconnectExtremity, String parFileName) {
        super(equipment, startTime, parFileName);
        this.disconnectOrigin = disconnectOrigin;
        this.disconnectExtremity = disconnectExtremity;
    }

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime, boolean disconnectOrigin, boolean disconnectExtremity) {
        this(equipment, startTime, disconnectOrigin, disconnectExtremity, null);
    }

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime, String parFileName) {
        this(equipment, startTime, true, true, parFileName);
    }

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime) {
        this(equipment, startTime, true, true, null);
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    private List<VarConnection> getVarConnectionsWithQuadripoleEquipment(QuadripoleDisconnectableEquipment connected) {
        return List.of(new VarConnection("event_state1_value", connected.getDisconnectableVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), QuadripoleDisconnectableEquipment.class, this::getVarConnectionsWithQuadripoleEquipment);
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectOrigin", Boolean.toString(disconnectOrigin));
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectExtremity", Boolean.toString(disconnectExtremity));
    }
}

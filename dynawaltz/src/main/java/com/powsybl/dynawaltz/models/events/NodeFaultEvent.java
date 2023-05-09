/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Bus;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class NodeFaultEvent extends AbstractEventModel {

    private static final String EVENT_PREFIX = "Node_Fault_";

    private final double faultTime;
    private final double rPu;
    private final double xPu;

    public NodeFaultEvent(Bus equipment, double startTime, double faultTime, double rPu, double xPu) {
        super(equipment, startTime, EVENT_PREFIX);
        this.faultTime = faultTime;
        this.rPu = rPu;
        this.xPu = xPu;
    }

    @Override
    public String getLib() {
        return "NodeFault";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), BusModel.class, this::getVarConnectionsWithBus, context);
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return List.of(new VarConnection("fault_terminal", connected.getTerminalVarName()));
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "fault_RPu", Double.toString(rPu));
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "fault_XPu", Double.toString(xPu));
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "fault_tBegin", Double.toString(getStartTime()));
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "fault_tEnd", Double.toString(getStartTime() + faultTime));
    }
}

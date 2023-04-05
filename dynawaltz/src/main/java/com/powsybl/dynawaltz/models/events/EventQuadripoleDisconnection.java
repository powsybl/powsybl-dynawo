/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.xml.ParametersXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.ParametersSet.ParameterType.BOOL;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventQuadripoleDisconnection extends AbstractEventModel {

    private final boolean disconnectOrigin;
    private final boolean disconnectExtremity;

    public EventQuadripoleDisconnection(String eventModelId, String lineStaticId, double startTime,
                                        boolean disconnectOrigin, boolean disconnectExtremity) {
        super(eventModelId, lineStaticId, startTime);
        this.disconnectOrigin = disconnectOrigin;
        this.disconnectExtremity = disconnectExtremity;
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    private List<VarConnection> getVarConnectionsWithLine(LineModel connected) {
        return List.of(new VarConnection("event_state1_value", connected.getStateValueVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipmentStaticId(), LineModel.class, this::getVarConnectionsWithLine, context);
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectOrigin", Boolean.toString(disconnectOrigin));
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectExtremity", Boolean.toString(disconnectExtremity));
    }
}

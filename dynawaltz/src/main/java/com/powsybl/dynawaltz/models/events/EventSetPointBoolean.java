/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.dynawaltz.xml.ParametersXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventSetPointBoolean extends AbstractEventModel {

    private final boolean stateEvent;

    public EventSetPointBoolean(String eventModelId, String generatorStaticId, double startTime, boolean stateEvent) {
        super(eventModelId, generatorStaticId, startTime);
        this.stateEvent = stateEvent;
    }

    @Override
    public String getLib() {
        return "EventSetPointBoolean";
    }

    private List<VarConnection> getVarConnectionsWithGenerator(GeneratorModel connected) {
        return List.of(new VarConnection("event_state1", connected.getSwitchOffSignalEventVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipmentStaticId(), GeneratorModel.class, this::getVarConnectionsWithGenerator, context);
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, BOOL, "event_stateEvent1", Boolean.toString(stateEvent));
    }
}

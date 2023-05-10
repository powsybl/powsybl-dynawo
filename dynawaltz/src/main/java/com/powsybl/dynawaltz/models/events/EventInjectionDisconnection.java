/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.InjectionModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.ShuntCompensator;
import com.powsybl.iidm.network.StaticVarCompensator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventInjectionDisconnection extends AbstractEventModel {

    private static final String EVENT_PREFIX = "Disconnect_";
    private static final String DYNAMIC_MODEL_LIB = "EventSetPointBoolean";
    private static final String DEFAULT_MODEL_LIB = "EventConnectedStatus";

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

    public EventInjectionDisconnection(StaticVarCompensator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime);
        this.disconnect = disconnect;
    }

    public EventInjectionDisconnection(StaticVarCompensator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    public EventInjectionDisconnection(ShuntCompensator equipment, double startTime, boolean disconnect) {
        super(equipment, startTime);
        this.disconnect = disconnect;
    }

    public EventInjectionDisconnection(ShuntCompensator equipment, double startTime) {
        this(equipment, startTime, true);
    }

    @Override
    public String getLib() {
        return null;
    }

    private List<VarConnection> getVarConnectionsWithInjectionModel(InjectionModel connected) {
        return List.of(new VarConnection("event_state1", connected.getSwitchOffSignalEventVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), InjectionModel.class, this::getVarConnectionsWithInjectionModel, context);
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "event_tEvent", Double.toString(getStartTime()));
        ParametersXml.writeParameter(writer, ParameterType.BOOL, "event_stateEvent1", Boolean.toString(disconnect));
    }

    @Override
    public String getName() {
        return EventInjectionDisconnection.class.getSimpleName();
    }

    @Override
    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", context.isWithoutBlackBoxDynamicModel(getEquipment().getId()) ? DEFAULT_MODEL_LIB : DYNAMIC_MODEL_LIB);
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
    }
}

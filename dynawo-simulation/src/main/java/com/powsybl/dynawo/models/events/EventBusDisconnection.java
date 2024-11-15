/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.ActionConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;
import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventBusDisconnection extends AbstractEvent implements ContextDependentEvent {

    private final boolean disconnect;
    private boolean hasDefaultModel;

    protected EventBusDisconnection(String eventId, Bus equipment, EventModelInfo eventModelInfo, double startTime, boolean disconnect) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.disconnect = disconnect;
    }

    private List<VarConnection> getVarConnectionsWith(ActionConnectionPoint connected) {
        return connected.getStateValueVarName()
                .map(sv -> List.of(new VarConnection("event_state1_value", sv)))
                .orElse(Collections.emptyList());
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (hasDefaultModel) {
            adder.createMacroConnections(this, getEquipment(), ActionConnectionPoint.class, this::getVarConnectionsWith);
        } else {
            DynawoSimulationReports.reportFailedDynamicModelHandling(adder.getReportNode(), getName(), getDynamicModelId(), IdentifiableType.BUS.toString());
        }
    }

    @Override
    public String getLib() {
        return "EventConnectedStatus";
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder) {
        if (hasDefaultModel) {
            ParametersSet paramSet = new ParametersSet(getParameterSetId());
            createEventSpecificParameters(paramSet);
            parametersAdder.accept(paramSet);
        }
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("event_open", BOOL, Boolean.toString(disconnect));
    }

    @Override
    public final void setEquipmentHasDynamicModel(DynawoSimulationContext context) {
        hasDefaultModel = !hasDynamicModel(context);
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (hasDefaultModel) {
            super.write(writer, parFileName);
        }
    }
}

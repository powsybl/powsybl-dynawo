/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Generator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EventReferenceVoltageVariation extends AbstractEvent {

    protected final double deltaU;
    protected boolean isConnected = true;

    protected EventReferenceVoltageVariation(String eventId, Generator equipment, EventModelInfo eventModelInfo, double startTime, double deltaU) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.deltaU = deltaU;
    }

    private List<VarConnection> getVarConnectionsWith(UControllableEquipmentModel connected) {
        return List.of(new VarConnection("step_step_value", connected.getDeltaUVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        isConnected = !adder.createMacroConnectionsOrSkip(this, getEquipment(), UControllableEquipmentModel.class, this::getVarConnectionsWith);
        if (!isConnected) {
            DynawoSimulationReports.reportEmptyEvent(adder.getReportNode(), getDynamicModelId(), UControllableEquipmentModel.class.getSimpleName());
        }
    }

    @Override
    public String getName() {
        return EventReferenceVoltageVariation.class.getSimpleName();
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (isConnected) {
            super.write(writer, parFileName);
        }
    }

    @Override
    public void createDynamicModelParameters(Consumer<ParametersSet> parametersAdder) {
        if (isConnected) {
            super.createDynamicModelParameters(parametersAdder);
        }
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("step_Value0", DOUBLE, "0.0");
        paramSet.addParameter("step_tStep", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("step_Height", DOUBLE, Double.toString(deltaU));
    }
}

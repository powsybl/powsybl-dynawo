/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.utils.ImmutableLateInit;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Injection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;
import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Riad Benradi {@benradiria <riad.benradi at rte-france.com>}
 */
public class EventReactivePowerVariation extends AbstractEvent implements ContextDependentEvent {

    private static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    private enum EquipmentModelType {
        SPECIFIED,
        DEFAULT_GENERATOR,
        DEFAULT_LOAD;

        public boolean isSpecified() {
            return this == SPECIFIED;
        }
    }

    private final double deltaQ;
    private final ImmutableLateInit<EquipmentModelType> equipmentModelType = new ImmutableLateInit<>();

    protected EventReactivePowerVariation(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, double deltaQ) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.deltaQ = deltaQ;
    }

    @Override
    public String getLib() {
        if (equipmentModelType.getValue().isSpecified()) {
            return super.getLib();
        }

        return DEFAULT_MODEL_LIB;
    }

    @Override
    public String getName() {
        return EventReactivePowerVariation.class.getSimpleName();
    }

    private List<VarConnection> getVarConnectionsWith(ControllableQEquipmentModel connected) {
        if (equipmentModelType.getValue().isSpecified()) {
            return List.of(new VarConnection("step_step_value", connected.getDeltaQVarName()));
        }
        return List.of(new VarConnection("event_state1", connected.getDeltaQVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this,
                getEquipment(),
                ControllableQEquipmentModel.class,
                this::getVarConnectionsWith);
    }

    @Override
    public void createDynamicModelParameters(Consumer<ParametersSet> parametersAdder) {
        super.createDynamicModelParameters(parametersAdder);
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        if (equipmentModelType.getValue().isSpecified()) {
            paramSet.addParameter("step_Value0", DOUBLE, Double.toString(0));
            paramSet.addParameter("step_tStep", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("step_Height", DOUBLE, Double.toString(deltaQ));
        } else {
            paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("event_stateEvent1", DOUBLE, Double.toString(deltaQ));
        }
    }

    @Override
    public final void setEquipmentModelType(boolean hasDynamicModel) {
        if (hasDynamicModel) {
            equipmentModelType.setValue(EquipmentModelType.SPECIFIED);
        } else if (IdentifiableType.GENERATOR == getEquipment().getType()) {
            equipmentModelType.setValue(EquipmentModelType.DEFAULT_GENERATOR);
        } else if (IdentifiableType.LOAD == getEquipment().getType()) {
            equipmentModelType.setValue(EquipmentModelType.DEFAULT_LOAD);
        }
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        if (equipmentModelType.getValue().equals(EventReactivePowerVariation.EquipmentModelType.DEFAULT_LOAD)) {
            networkParameters.addParameter(getEquipment().getId() + "_isControllableQ", BOOL, Boolean.toString(true));
        }
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        super.write(writer, parFileName);
    }
}

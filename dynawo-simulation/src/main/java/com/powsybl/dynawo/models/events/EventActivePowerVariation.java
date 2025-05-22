/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.DynawoSimulationReports;
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

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariation extends AbstractEvent implements ContextDependentEvent {

    private static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    private enum EquipmentModelType {
        SPECIFIED,
        DEFAULT_GENERATOR,
        DEFAULT_LOAD;

        public boolean isSupported() {
            return this != DEFAULT_LOAD;
        }
    }

    private final double deltaP;
    private final ImmutableLateInit<EquipmentModelType> equipmentModelType = new ImmutableLateInit<>();

    protected EventActivePowerVariation(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, double deltaP) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.deltaP = deltaP;
    }

    @Override
    public String getLib() {
        return switch (equipmentModelType.getValue()) {
            case SPECIFIED -> super.getLib();
            case DEFAULT_GENERATOR -> DEFAULT_MODEL_LIB;
            case DEFAULT_LOAD -> null;
        };
    }

    @Override
    public String getName() {
        return EventActivePowerVariation.class.getSimpleName();
    }

    private List<VarConnection> getVarConnectionsWith(ControllableEquipmentModel connected) {
        return switch (equipmentModelType.getValue()) {
            case SPECIFIED -> List.of(new VarConnection("step_step_value", connected.getDeltaPVarName()));
            case DEFAULT_GENERATOR -> List.of(new VarConnection("event_state1", connected.getDeltaPVarName()));
            default -> List.of();
        };
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (equipmentModelType.getValue().isSupported()) {
            adder.createMacroConnections(this,
                    getEquipment(),
                    ControllableEquipmentModel.class,
                    this::getVarConnectionsWith);
        } else {
            DynawoSimulationReports.reportFailedDefaultModelHandling(adder.getReportNode(), getName(), getDynamicModelId(), IdentifiableType.LOAD.toString());
        }
    }

    @Override
    public void createDynamicModelParameters(Consumer<ParametersSet> parametersAdder) {
        if (equipmentModelType.getValue().isSupported()) {
            super.createDynamicModelParameters(parametersAdder);
        }
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        switch (equipmentModelType.getValue()) {
            case SPECIFIED -> {
                paramSet.addParameter("step_Value0", DOUBLE, Double.toString(0));
                paramSet.addParameter("step_tStep", DOUBLE, Double.toString(getStartTime()));
                paramSet.addParameter("step_Height", DOUBLE, Double.toString(deltaP));
            }
            case DEFAULT_GENERATOR -> {
                paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
                paramSet.addParameter("event_stateEvent1", DOUBLE, Double.toString(deltaP));
            }
            default -> {
            }
        }
    }

    @Override
    public final void setEquipmentModelType(boolean hasDynamicModel) {
        if (hasDynamicModel) {
            equipmentModelType.setValue(EquipmentModelType.SPECIFIED);
        } else if (IdentifiableType.GENERATOR == getEquipment().getType()) {
            equipmentModelType.setValue(EquipmentModelType.DEFAULT_GENERATOR);
        } else if (IdentifiableType.LOAD == getEquipment().getType()) {
            // Default load is currently not supported
            equipmentModelType.setValue(EquipmentModelType.DEFAULT_LOAD);
        }
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (equipmentModelType.getValue().isSupported()) {
            super.write(writer, parFileName);
        }
    }
}

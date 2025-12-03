/**
 *
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.utils.ImmutableLateInit;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Generator;

import java.util.List;

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EventReferenceVoltageVariation extends AbstractEvent implements ContextDependentEvent {
    protected static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    protected enum EquipmentModelType {
        SPECIFIED(true),
        DEFAULT_GENERATOR(false);

        private final boolean isStep;

        EquipmentModelType(boolean isStep) {
            this.isStep = isStep;
        }

        public boolean isStep() {
            return isStep;
        }
    }

    protected final double deltaU;
    protected final ImmutableLateInit<EquipmentModelType> equipmentModelType = new ImmutableLateInit<>();

    protected EventReferenceVoltageVariation(String eventId, Generator equipment, EventModelInfo eventModelInfo, double startTime, double deltaU) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.deltaU = deltaU;
    }

    @Override
    public String getLib() {
        return equipmentModelType.getValue().isStep() ? super.getLib() : DEFAULT_MODEL_LIB;
    }

    @Override
    public final void setEquipmentModelType(boolean hasDynamicModel) {
        if (hasDynamicModel) {
            equipmentModelType.setValue(EquipmentModelType.SPECIFIED);
        } else {
            equipmentModelType.setValue(EquipmentModelType.DEFAULT_GENERATOR);
        }
    }

    private List<VarConnection> getVarConnectionsWith(UControllableEquipmentModel connected) {
        if (equipmentModelType.getValue().isStep()) {
            return List.of(new VarConnection("step_step_value", connected.getDeltaUVarName()));
        }
        return List.of(new VarConnection("event_state1", connected.getDeltaUVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this,
                getEquipment(),
                UControllableEquipmentModel.class,
                this::getVarConnectionsWith);
    }

    @Override
    public String getName() {
        return EventReferenceVoltageVariation.class.getSimpleName();
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        if (equipmentModelType.getValue().isStep()) {
            paramSet.addParameter("step_Value0", DOUBLE, "0.0");
            paramSet.addParameter("step_tStep", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("step_Height", DOUBLE, Double.toString(deltaU));
        } else {
            paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("event_stateEvent1", DOUBLE, Double.toString(deltaU));
        }
    }

}

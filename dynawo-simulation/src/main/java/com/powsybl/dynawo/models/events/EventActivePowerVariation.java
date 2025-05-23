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

import java.util.EnumSet;
import java.util.List;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;
import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;
import static java.lang.Boolean.TRUE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariation extends AbstractEvent implements ContextDependentEvent {

    private static final EnumSet<IdentifiableType> CONNECTABLE_EQUIPMENTS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD);
    private static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    private final double deltaP;
    private final ImmutableLateInit<Boolean> equipmentHasDynamicModel = new ImmutableLateInit<>();

    protected EventActivePowerVariation(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, double deltaP) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.deltaP = deltaP;
    }

    public static boolean isConnectable(IdentifiableType type) {
        return CONNECTABLE_EQUIPMENTS.contains(type);
    }

    @Override
    public String getLib() {
        return TRUE == equipmentHasDynamicModel.getValue() ? super.getLib() : DEFAULT_MODEL_LIB;
    }

    @Override
    public String getName() {
        return EventActivePowerVariation.class.getSimpleName();
    }

    private List<VarConnection> getVarConnectionsWith(ControllableEquipmentModel connected) {
        return List.of(TRUE == equipmentHasDynamicModel.getValue() ? new VarConnection("step_step_value", connected.getDeltaPVarName())
                : new VarConnection("event_state1", connected.getDeltaPVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this,
                getEquipment(),
                ControllableEquipmentModel.class,
                this::getVarConnectionsWith);
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        if (TRUE == equipmentHasDynamicModel.getValue()) {
            paramSet.addParameter("step_Value0", DOUBLE, Double.toString(0));
            paramSet.addParameter("step_tStep", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("step_Height", DOUBLE, Double.toString(deltaP));
        } else {
            paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
            paramSet.addParameter("event_stateEvent1", DOUBLE, Double.toString(deltaP));
        }
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        if (getEquipment().getType() == IdentifiableType.LOAD) {
            networkParameters.addParameter(getEquipment().getId() + "_isControllable", BOOL, Boolean.toString(true));
        }
    }

    @Override
    public final void setEquipmentHasDynamicModel(boolean hasDynamicModel) {
        this.equipmentHasDynamicModel.setValue(hasDynamicModel);
    }
}

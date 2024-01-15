/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.utils.ImmutableLateInit;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Injection;

import java.util.EnumSet;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;
import static java.lang.Boolean.TRUE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariation extends AbstractEvent implements ContextDependentEvent {

    private static final EnumSet<IdentifiableType> CONNECTABLE_EQUIPMENTS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD);
    private static final String EVENT_PREFIX = "Step_";
    private static final String DYNAMIC_MODEL_LIB = "Step";
    private static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    private final double deltaP;
    private final ImmutableLateInit<Boolean> equipmentHasDynamicModel = new ImmutableLateInit<>();

    protected EventActivePowerVariation(Injection<?> equipment, double startTime, double deltaP) {
        super(equipment, startTime, EVENT_PREFIX, DYNAMIC_MODEL_LIB);
        this.deltaP = deltaP;
    }

    public static boolean isConnectable(IdentifiableType type) {
        return CONNECTABLE_EQUIPMENTS.contains(type);
    }

    @Override
    public String getLib() {
        return TRUE == equipmentHasDynamicModel.getValue() ? DYNAMIC_MODEL_LIB : DEFAULT_MODEL_LIB;
    }

    @Override
    public String getName() {
        return EventActivePowerVariation.class.getSimpleName();
    }

    private List<VarConnection> getVarConnectionsWith(ControllableEquipment connected) {
        return List.of(TRUE == equipmentHasDynamicModel.getValue() ? new VarConnection("step_step_value", connected.getDeltaPVarName())
                : new VarConnection("event_state1", connected.getDeltaPVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this,
                getEquipment(),
                ControllableEquipment.class,
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
    public final void setEquipmentHasDynamicModel(DynaWaltzContext context) {
        this.equipmentHasDynamicModel.setValue(hasDynamicModel(context));
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.models.utils.ImmutableLateInit;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;
import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;
import static java.lang.Boolean.TRUE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicLibEventDisconnection extends AbstractEvent implements ContextDependentEvent {

    protected static final String DYNAMIC_MODEL_LIB = "EventSetPointBoolean";
    protected static final String DEFAULT_MODEL_LIB = "EventConnectedStatus";
    protected static final String DISCONNECTION_VAR_CONNECT = "event_state1";

    protected final boolean disconnect;
    private final ImmutableLateInit<Boolean> equipmentHasDynamicModel = new ImmutableLateInit<>();

    protected AbstractDynamicLibEventDisconnection(String eventId, Identifiable<?> equipment, EventModelInfo eventModelInfo, double startTime, boolean disconnect) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.disconnect = disconnect;
    }

    @Override
    public String getLib() {
        return TRUE == equipmentHasDynamicModel.getValue() ? DYNAMIC_MODEL_LIB : DEFAULT_MODEL_LIB;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("event_stateEvent1", BOOL, Boolean.toString(disconnect));
    }

    @Override
    public final void setEquipmentHasDynamicModel(boolean hasDynamicModel) {
        this.equipmentHasDynamicModel.setValue(hasDynamicModel);
    }
}

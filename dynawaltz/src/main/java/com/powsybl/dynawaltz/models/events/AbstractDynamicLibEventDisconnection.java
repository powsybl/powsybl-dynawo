/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.utils.ImmutableLateInit;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;
import static java.lang.Boolean.TRUE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicLibEventDisconnection extends AbstractEvent implements ContextDependentEvent {

    private static final String EVENT_PREFIX = "Disconnect_";
    private static final String DYNAMIC_MODEL_LIB = "EventSetPointBoolean";
    private static final String DEFAULT_MODEL_LIB = "EventConnectedStatus";
    protected static final String DISCONNECTION_VAR_CONNECT = "event_state1";

    private final boolean disconnect;
    private final ImmutableLateInit<Boolean> equipmentHasDynamicModel = new ImmutableLateInit<>();

    protected AbstractDynamicLibEventDisconnection(Identifiable<?> equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, EVENT_PREFIX);
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
    public final void setEquipmentHasDynamicModel(DynaWaltzContext context) {
        this.equipmentHasDynamicModel.setValue(hasDynamicModel(context));
    }
}

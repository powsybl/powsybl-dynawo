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

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicLibEventDisconnection extends AbstractEvent implements ContextDependentEvent {

    protected enum EquipmentModelType {

        SPECIFIED("EventSetPointBoolean", "event_state1", "event_stateEvent1"),
        DEFAULT("EventConnectedStatus", "event_state1_value", "event_open"),;

        private final String lib;
        private final String varConnection;
        private final String parameterName;

        EquipmentModelType(String lib, String varConnection, String parameterName) {
            this.lib = lib;
            this.varConnection = varConnection;
            this.parameterName = parameterName;
        }

        public String getLib() {
            return lib;
        }

        public String getVarConnection() {
            return varConnection;
        }

        public String getParameterName() {
            return parameterName;
        }
    }

    protected final boolean disconnect;
    protected final ImmutableLateInit<EquipmentModelType> equipmentModelType = new ImmutableLateInit<>();

    protected AbstractDynamicLibEventDisconnection(String eventId, Identifiable<?> equipment, EventModelInfo eventModelInfo, double startTime, boolean disconnect) {
        super(eventId, equipment, eventModelInfo, startTime);
        this.disconnect = disconnect;
    }

    @Override
    public String getLib() {
        return equipmentModelType.getValue().getLib();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter(equipmentModelType.getValue().getParameterName(), BOOL, Boolean.toString(disconnect));
    }

    @Override
    public final void setEquipmentModelType(boolean hasDynamicModel) {
        this.equipmentModelType.setValue(hasDynamicModel ? EquipmentModelType.SPECIFIED : EquipmentModelType.DEFAULT);
    }
}

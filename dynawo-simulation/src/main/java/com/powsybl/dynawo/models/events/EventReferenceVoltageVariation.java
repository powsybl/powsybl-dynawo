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
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Injection;

import java.util.List;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EventReferenceVoltageVariation extends AbstractEventPower {

    protected EventReferenceVoltageVariation(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, double deltaU) {
        super(eventId, equipment, eventModelInfo, startTime, deltaU);
    }

    private List<VarConnection> getVarConnectionsWith(ControllableUEquipmentModel connected) {
        if (equipmentModelType.getValue().isSpecified()) {
            return List.of(new VarConnection("step_step_value", connected.getDeltaUVarName()));
        }

        return List.of(new VarConnection("event_state1", connected.getDeltaUVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this,
                getEquipment(),
                ControllableUEquipmentModel.class,
                this::getVarConnectionsWith);
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        if (equipmentModelType.getValue().equals(EquipmentModelType.DEFAULT_LOAD)) {
            networkParameters.addParameter(getEquipment().getId() + "_isControllableU", BOOL, Boolean.toString(true));
        }
    }

    @Override
    public String getName() {
        return EventReferenceVoltageVariation.class.getSimpleName();
    }
}

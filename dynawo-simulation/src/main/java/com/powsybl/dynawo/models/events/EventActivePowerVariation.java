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
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Injection;

import java.util.List;

import static com.powsybl.dynawo.parameters.ParameterType.BOOL;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariation extends AbstractVariationEvent {

    protected EventActivePowerVariation(String eventId, Injection<?> equipment, EventModelInfo eventModelInfo, double startTime, double deltaP) {
        super(eventId, equipment, eventModelInfo, startTime, deltaP);
    }

    private List<VarConnection> getVarConnectionsWith(PControllableEquipmentModel connected) {
        if (equipmentModelType.getValue().isStep()) {
            return List.of(new VarConnection("step_step_value", connected.getDeltaPVarName()));
        }
        return List.of(new VarConnection("event_state1", connected.getDeltaPVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        isSkipped = adder.createMacroConnectionsOrSkip(this, getEquipment(), PControllableEquipmentModel.class, this::getVarConnectionsWith);
        if (isSkipped) {
            DynawoSimulationReports.reportFailedDynamicModelHandling(adder.getReportNode(), getName(), getDynamicModelId(), getEquipment().getType().toString());
        }
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        if (equipmentModelType.getValue().equals(EquipmentModelType.DEFAULT_LOAD) && !isSkipped) {
            networkParameters.addParameter(getEquipment().getId() + "_isPControllable", BOOL, Boolean.toString(true));
        }
    }

    @Override
    public String getName() {
        return EventActivePowerVariation.class.getSimpleName();
    }
}

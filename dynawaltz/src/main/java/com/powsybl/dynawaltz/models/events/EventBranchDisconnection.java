/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.automatons.BranchModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventBranchDisconnection extends AbstractEvent {

    private final TwoSides disconnectSide;

    protected EventBranchDisconnection(String eventId, Branch<?> equipment, double startTime, TwoSides disconnectSide) {
        super(eventId, equipment, startTime, "EventQuadripoleDisconnection");
        this.disconnectSide = disconnectSide;
    }

    private List<VarConnection> getVarConnectionsWith(BranchModel connected) {
        return List.of(new VarConnection("event_state1_value", connected.getStateValueVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), BranchModel.class, this::getVarConnectionsWith);
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("event_disconnectOrigin", BOOL, Boolean.toString(disconnectSide == null || TwoSides.ONE == disconnectSide));
        paramSet.addParameter("event_disconnectExtremity", BOOL, Boolean.toString(disconnectSide == null || TwoSides.TWO == disconnectSide));
    }

    @Override
    public String getName() {
        return "EventBranchDisconnection";
    }
}

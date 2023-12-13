/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.ActionConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Bus;

import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NodeFaultEvent extends AbstractEvent {

    private static final String EVENT_PREFIX = "Node_Fault_";

    private final double faultTime;
    private final double rPu;
    private final double xPu;

    public NodeFaultEvent(Bus equipment, double startTime, double faultTime, double rPu, double xPu) {
        super(equipment, startTime, EVENT_PREFIX);
        this.faultTime = faultTime;
        this.rPu = rPu;
        this.xPu = xPu;
    }

    @Override
    public String getLib() {
        return "NodeFault";
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, getEquipment(), ActionConnectionPoint.class, this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(ActionConnectionPoint connected) {
        return List.of(new VarConnection("fault_terminal", connected.getTerminalVarName()));
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet) {
        paramSet.addParameter("fault_RPu", DOUBLE, Double.toString(rPu));
        paramSet.addParameter("fault_XPu", DOUBLE, Double.toString(xPu));
        paramSet.addParameter("fault_tBegin", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("fault_tEnd", DOUBLE, Double.toString(getStartTime() + faultTime));
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        networkParameters.addParameter(getEquipment().getId() + "_hasShortCircuitCapabilities", BOOL, Boolean.toString(true));
    }
}

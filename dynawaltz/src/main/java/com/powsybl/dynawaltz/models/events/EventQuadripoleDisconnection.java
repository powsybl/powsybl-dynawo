/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.automatons.QuadripoleModel;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Branch;

import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventQuadripoleDisconnection extends AbstractEvent {

    private static final String EVENT_PREFIX = "Disconnect_";
    private final boolean disconnectOrigin;
    private final boolean disconnectExtremity;

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime, boolean disconnectOrigin, boolean disconnectExtremity) {
        super(equipment, startTime, EVENT_PREFIX);
        this.disconnectOrigin = disconnectOrigin;
        this.disconnectExtremity = disconnectExtremity;
    }

    public EventQuadripoleDisconnection(Branch<?> equipment, double startTime) {
        this(equipment, startTime, true, true);
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    private List<VarConnection> getVarConnectionsWith(QuadripoleModel connected) {
        return List.of(new VarConnection("event_state1_value", connected.getStateValueVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), QuadripoleModel.class, this::getVarConnectionsWith, context);
    }

    @Override
    protected void createEventSpecificParameters(ParametersSet paramSet, DynaWaltzContext context) {
        paramSet.addParameter("event_tEvent", DOUBLE, Double.toString(getStartTime()));
        paramSet.addParameter("event_disconnectOrigin", BOOL, Boolean.toString(disconnectOrigin));
        paramSet.addParameter("event_disconnectExtremity", BOOL, Boolean.toString(disconnectExtremity));
    }
}

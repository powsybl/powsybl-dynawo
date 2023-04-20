/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.*;

import java.util.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class CurrentLimitAutomaton extends CurrentLimitTwoLevelsAutomaton {

    private final Side side;

    public CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, Branch<?> quadripoleEquipment, Side side) {
        super(dynamicModelId, parameterSetId, quadripoleEquipment);
        this.side = Objects.requireNonNull(side);
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), QuadripoleModel.class, this::getVarConnectionsWithQuadripole, context, side);
    }

    private List<VarConnection> getVarConnectionsWithQuadripole(QuadripoleModel connected, Side side) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connected.getIVarName(side)),
                new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }
}

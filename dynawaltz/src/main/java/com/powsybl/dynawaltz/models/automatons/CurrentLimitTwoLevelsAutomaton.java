/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.*;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class CurrentLimitTwoLevelsAutomaton extends AbstractPureDynamicBlackBoxModel {

    private final Branch<?> quadripoleEquipment;

    public CurrentLimitTwoLevelsAutomaton(String dynamicModelId, String parameterSetId, Branch<?> quadripoleEquipment) {
        super(dynamicModelId, parameterSetId);
        this.quadripoleEquipment = Objects.requireNonNull(quadripoleEquipment);
    }

    protected final Identifiable<?> getEquipment() {
        return quadripoleEquipment;
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomatonTwoLevels";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(), QuadripoleModel.class, this::getVarConnectionsWithQuadripole, context);
    }

    private List<VarConnection> getVarConnectionsWithQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored1", connected.getIVarName(Side.ONE)),
                new VarConnection("currentLimitAutomaton_IMonitored2", connected.getIVarName(Side.TWO)),
                new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }
}

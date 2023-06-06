/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.Branch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class CurrentLimitTwoLevelsAutomaton extends CurrentLimitAutomaton {

    protected static final String FIRST_MEASURE_SUFFIX = MEASURE_SUFFIX + "1";
    protected static final String SECOND_MEASURE_SUFFIX = MEASURE_SUFFIX + "2";

    private final Branch<?> secondMeasuredQuadripole;
    private final Side secondMeasuredSide;

    public CurrentLimitTwoLevelsAutomaton(String dynamicModelId, String parameterSetId, Branch<?> measuredQuadripole, Side measuredSide, Branch<?> secondMeasuredQuadripole, Side secondMeasuredSide, Branch<?> controlledQuadripole, String lib) {
        super(dynamicModelId, parameterSetId, measuredQuadripole, measuredSide, controlledQuadripole, lib);
        this.secondMeasuredQuadripole = Objects.requireNonNull(secondMeasuredQuadripole);
        this.secondMeasuredSide = Objects.requireNonNull(secondMeasuredSide);
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(measuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithMeasuredQuadripole, context, FIRST_MEASURE_SUFFIX + measuredSide.getSideSuffix());
        createMacroConnections(secondMeasuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithSecondMeasuredQuadripole, context, SECOND_MEASURE_SUFFIX + secondMeasuredSide.getSideSuffix());
        createMacroConnections(controlledQuadripole, QuadripoleModel.class, this::getVarConnectionsWithControlledQuadripole, context, CONTROL_SUFFIX);
    }

    @Override
    protected List<VarConnection> getVarConnectionsWithMeasuredQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored1", connected.getIVarName(measuredSide)),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    private List<VarConnection> getVarConnectionsWithSecondMeasuredQuadripole(QuadripoleModel connected) {
        return List.of(new VarConnection("currentLimitAutomaton_IMonitored2", connected.getIVarName(secondMeasuredSide)));
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.currentLimits;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.automatons.QuadripoleModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.utils.SideUtils;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurrentLimitTwoLevelsAutomaton extends CurrentLimitAutomaton {

    protected static final String FIRST_MEASURE_SUFFIX = MEASURE_SUFFIX + "1";
    protected static final String SECOND_MEASURE_SUFFIX = MEASURE_SUFFIX + "2";

    private final Branch<?> secondMeasuredQuadripole;
    private final TwoSides secondMeasuredSide;

    CurrentLimitTwoLevelsAutomaton(String dynamicModelId, String parameterSetId, Branch<?> measuredQuadripole, TwoSides measuredSide, Branch<?> secondMeasuredQuadripole, TwoSides secondMeasuredSide, Branch<?> controlledQuadripole, String lib) {
        super(dynamicModelId, parameterSetId, measuredQuadripole, measuredSide, controlledQuadripole, lib);
        this.secondMeasuredQuadripole = Objects.requireNonNull(secondMeasuredQuadripole);
        this.secondMeasuredSide = Objects.requireNonNull(secondMeasuredSide);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, measuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithFirstMeasuredQuadripole, FIRST_MEASURE_SUFFIX + SideUtils.getSideSuffix(measuredSide));
        adder.createMacroConnections(this, secondMeasuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithSecondMeasuredQuadripole, SECOND_MEASURE_SUFFIX + SideUtils.getSideSuffix(secondMeasuredSide));
        adder.createMacroConnections(this, controlledQuadripole, QuadripoleModel.class, this::getVarConnectionsWithControlledQuadripole, CONTROL_SUFFIX);
    }

    private List<VarConnection> getVarConnectionsWithFirstMeasuredQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored1", connected.getIVarName(measuredSide)),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    private List<VarConnection> getVarConnectionsWithSecondMeasuredQuadripole(QuadripoleModel connected) {
        return List.of(new VarConnection("currentLimitAutomaton_IMonitored2", connected.getIVarName(secondMeasuredSide)));
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.overloadmanagments;

import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.automationsystems.BranchModel;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicTwoLevelsOverloadManagementSystem extends DynamicOverloadManagementSystem {

    protected static final String FIRST_MEASURE_SUFFIX = MEASURE_SUFFIX + "1";
    protected static final String SECOND_MEASURE_SUFFIX = MEASURE_SUFFIX + "2";

    private final Branch<?> secondMeasuredBranch;
    private final TwoSides secondMeasuredSide;

    protected DynamicTwoLevelsOverloadManagementSystem(String dynamicModelId, String parameterSetId, Branch<?> measuredBranch, TwoSides measuredSide, Branch<?> secondMeasuredBranch, TwoSides secondMeasuredSide, Branch<?> controlledBranch, String lib) {
        super(dynamicModelId, parameterSetId, measuredBranch, measuredSide, controlledBranch, lib);
        this.secondMeasuredBranch = Objects.requireNonNull(secondMeasuredBranch);
        this.secondMeasuredSide = Objects.requireNonNull(secondMeasuredSide);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, measuredBranch, BranchModel.class, this::getVarConnectionsWithFirstMeasuredBranch, FIRST_MEASURE_SUFFIX + SideUtils.getSideSuffix(measuredSide));
        adder.createMacroConnections(this, secondMeasuredBranch, BranchModel.class, this::getVarConnectionsWithSecondMeasuredBranch, SECOND_MEASURE_SUFFIX + SideUtils.getSideSuffix(secondMeasuredSide));
        adder.createMacroConnections(this, controlledBranch, BranchModel.class, this::getVarConnectionsWithControlledBranch, CONTROL_SUFFIX);
    }

    private List<VarConnection> getVarConnectionsWithFirstMeasuredBranch(BranchModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored1", connected.getIVarName(measuredSide)),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    private List<VarConnection> getVarConnectionsWithSecondMeasuredBranch(BranchModel connected) {
        return List.of(new VarConnection("currentLimitAutomaton_IMonitored2", connected.getIVarName(secondMeasuredSide)));
    }
}

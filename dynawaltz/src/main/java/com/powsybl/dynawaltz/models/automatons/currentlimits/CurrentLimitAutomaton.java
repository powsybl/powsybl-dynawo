/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.automatons.currentlimits;

import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
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
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurrentLimitAutomaton extends AbstractPureDynamicBlackBoxModel {

    protected static final String MEASURE_SUFFIX = "Measure";
    protected static final String CONTROL_SUFFIX = "Control";

    protected final Branch<?> measuredQuadripole;
    protected final TwoSides measuredSide;
    protected final Branch<?> controlledQuadripole;

    protected CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, Branch<?> measuredQuadripole, TwoSides measuredSide, Branch<?> controlledQuadripole, String lib) {
        super(dynamicModelId, parameterSetId, lib);
        this.measuredQuadripole = Objects.requireNonNull(measuredQuadripole);
        this.measuredSide = Objects.requireNonNull(measuredSide);
        this.controlledQuadripole = Objects.requireNonNull(controlledQuadripole);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, measuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithMeasuredQuadripole, MEASURE_SUFFIX + SideUtils.getSideSuffix(measuredSide));
        adder.createMacroConnections(this, controlledQuadripole, QuadripoleModel.class, this::getVarConnectionsWithControlledQuadripole, CONTROL_SUFFIX);
    }

    private List<VarConnection> getVarConnectionsWithMeasuredQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connected.getIVarName(measuredSide)),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    protected List<VarConnection> getVarConnectionsWithControlledQuadripole(QuadripoleModel connected) {
        return List.of(new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()));
    }
}

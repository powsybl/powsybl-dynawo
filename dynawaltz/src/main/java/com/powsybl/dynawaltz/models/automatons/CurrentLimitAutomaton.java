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

    private static final String MEASURE_SUFFIX = "Measure";
    private static final String CONTROL_SUFFIX = "Control";

    private final Branch<?> measuredQuadripole;
    private final Side measuredSide;
    private final Branch<?> controlledQuadripole;

    public CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, Branch<?> measuredQuadripole, Side measuredSide, Branch<?> controlledQuadripole) {
        super(dynamicModelId, parameterSetId);
        this.measuredQuadripole = Objects.requireNonNull(measuredQuadripole);
        this.measuredSide = Objects.requireNonNull(measuredSide);
        this.controlledQuadripole = Objects.requireNonNull(controlledQuadripole);
    }

    public CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, Branch<?> measuredQuadripole, Side measuredSide) {
        this(dynamicModelId, parameterSetId, measuredQuadripole, measuredSide, measuredQuadripole);
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        // getEquipment()
        createMacroConnections(measuredQuadripole, QuadripoleModel.class, this::getVarConnectionsWithMeasuredQuadripole, context, MEASURE_SUFFIX + measuredSide.getSideSuffix());
        createMacroConnections(controlledQuadripole, QuadripoleModel.class, this::getVarConnectionsWithControlledQuadripole, context, CONTROL_SUFFIX);
    }

    private List<VarConnection> getVarConnectionsWithMeasuredQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connected.getIVarName(measuredSide)),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    private List<VarConnection> getVarConnectionsWithControlledQuadripole(QuadripoleModel connected) {
        return List.of(new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()));
    }
}

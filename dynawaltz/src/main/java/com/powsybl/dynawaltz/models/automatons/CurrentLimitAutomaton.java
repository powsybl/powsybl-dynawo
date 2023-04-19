/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class CurrentLimitAutomaton extends AbstractPureDynamicBlackBoxModel {

    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER);

    private final Identifiable<?> quadripoleEquipment;
    private final Side side;

    public CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, Line line, Side side) {
        super(dynamicModelId, parameterSetId);
        this.quadripoleEquipment = Objects.requireNonNull(line);
        this.side = Objects.requireNonNull(side);
    }

    public CurrentLimitAutomaton(String dynamicModelId, String parameterSetId, TwoWindingsTransformer transformer, Side side) {
        super(dynamicModelId, parameterSetId);
        this.quadripoleEquipment = Objects.requireNonNull(transformer);
        this.side = Objects.requireNonNull(side);
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(quadripoleEquipment, QuadripoleModel.class, this::getVarConnectionsWithQuadripole, context);
    }

    private List<VarConnection> getVarConnectionsWithQuadripole(QuadripoleModel connected) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connected.getIVarName(side)),
                new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDeactivateCurrentLimitsVarName())
        );
    }

    public static boolean isCompatibleEquipment(IdentifiableType type) {
        return COMPATIBLE_EQUIPMENTS.contains(type);
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.lines.LineModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class CurrentLimitAutomaton extends AbstractPureDynamicBlackBoxModel {

    private final Side side;
    private final String lineStaticId;

    public CurrentLimitAutomaton(String dynamicModelId, String staticId, String parameterSetId, Side side) {
        super(dynamicModelId, parameterSetId);
        this.side = Objects.requireNonNull(side);
        this.lineStaticId = staticId;
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, lineStaticId, LineModel.class, this::getVarConnectionsWithLine, side);
    }

    private List<VarConnection> getVarConnectionsWithLine(LineModel connected, Side side) {
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connected.getIVarName(side)),
                new VarConnection("currentLimitAutomaton_order", connected.getStateVarName()),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connected.getDesactivateCurrentLimitsVarName())
        );
    }

    public String getLineStaticId() {
        return lineStaticId;
    }
}

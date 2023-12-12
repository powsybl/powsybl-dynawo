/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardLine extends AbstractEquipmentBlackBoxModel<Line> implements LineModel {

    public StandardLine(String dynamicModelId, Line line, String parameterSetId) {
        super(dynamicModelId, parameterSetId, line, "Line");
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(TwoSides side) {
        return "line_terminal" + side.getNum();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        equipment.getTerminals().forEach(t -> adder.createTerminalMacroConnections(this, t, this::getVarConnectionsWith, equipment.getSide(t)));
    }

    @Override
    public String getName() {
        return getDynamicModelId();
    }

    @Override
    public String getStateVarName() {
        return "line_state";
    }

    @Override
    public String getStateValueVarName() {
        return "line_state_value";
    }

    @Override
    public String getIVarName(TwoSides side) {
        throw new UnsupportedOperationException("i variable not implemented in StandardLine dynawo's model");
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        throw new UnsupportedOperationException("deactivateCurrentLimits variable not implemented in StandardLine dynawo's model");
    }
}

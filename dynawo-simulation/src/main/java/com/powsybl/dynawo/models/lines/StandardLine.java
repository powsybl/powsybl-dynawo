/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.lines;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.versionablevariable.Versionable;
import com.powsybl.dynawo.models.versionablevariable.VersionableVarsMapping;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariablesHandler;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

import static com.powsybl.dynawo.models.versionablevariable.VersionableVariablesHandler.DYNAWO_VERSION_1_8_0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardLine extends AbstractEquipmentBlackBoxModel<Line> implements LineModel {

    private static final VarMapping P1 = new VarMapping("line_P1Pu", "p1");
    private static final VarMapping Q1 = new VarMapping("line_Q1Pu", "q1");
    private static final VarMapping P2 = new VarMapping("line_P2Pu", "p2");
    private static final VarMapping Q2 = new VarMapping("line_Q2Pu", "q2");
    private static final VarMapping STATE = new VarMapping("line_state", "state");

    private static final VersionableVarsMapping VERSIONABLE_VAR_MAPPING = new VersionableVarsMapping(
            "LINE",
            new Versionable.Step<>(List.of(P1, Q1, P2, Q2, STATE)),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, List.of(
                    P1, Q1, P2, Q2, STATE,
                    new VarMapping("line_ISide1", "i1"),
                    new VarMapping("line_ISide2", "i2")))
    );

    protected StandardLine(Line line, String parameterSetId, ModelConfig modelConfig) {
        super(line, parameterSetId, modelConfig);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName(side)));
    }

    private String getTerminalVarName(TwoSides side) {
        return "line_terminal" + side.getNum();
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VersionableVariablesHandler.getInstance().getCurrentVarMapping(VERSIONABLE_VAR_MAPPING);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        equipment.getTerminals().forEach(t -> adder.createTerminalMacroConnections(this, t, this::getVarConnectionsWith, equipment.getSide(t)));
    }

    @Override
    public String getStateVarName() {
        return "line_state";
    }

    @Override
    public String getStateValueVarName() {
        return VersionableVariablesHandler.getInstance().getCurrentValue("VARIABLE_VALUE", "line_state");
    }

    @Override
    public String getIVarName(TwoSides side) {
        throw new UnsupportedOperationException("i variable not implemented in StandardLine dynawo's model");
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        throw new UnsupportedOperationException("deactivateCurrentLimits variable not implemented in StandardLine dynawo's model");
    }

    @Override
    public boolean needMandatoryDynamicModels() {
        return true;
    }
}

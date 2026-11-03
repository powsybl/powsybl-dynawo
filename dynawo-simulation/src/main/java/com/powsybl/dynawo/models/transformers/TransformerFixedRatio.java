/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.transformers;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.dynawo.models.versionablevariable.Versionable;
import com.powsybl.dynawo.models.versionablevariable.VersionableHandler;
import com.powsybl.dynawo.models.versionablevariable.VersionableVarsMapping;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.HIGH_VOLTAGE;
import static com.powsybl.dynawo.models.versionablevariable.GlobalVersionableVariables.VARIABLE_VALUE;
import static com.powsybl.dynawo.models.versionablevariable.VersionableHandler.DYNAWO_VERSION_1_8_0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatio extends AbstractEquipmentBlackBoxModel<TwoWindingsTransformer> implements TransformerModel, TapChangerModel {

    private static final VarMapping P1 = new VarMapping("transformer_P1Pu", "p1");
    private static final VarMapping Q1 = new VarMapping("transformer_Q1Pu", "q1");
    private static final VarMapping P2 = new VarMapping("transformer_P2Pu", "p2");
    private static final VarMapping Q2 = new VarMapping("transformer_Q2Pu", "q2");
    private static final VarMapping STATE = new VarMapping("transformer_state", "state");

    private static final VersionableVarsMapping VERSIONABLE_VAR_MAPPING = new VersionableVarsMapping(
            "TFO",
            new Versionable.Step<>(List.of(P1, Q1, P2, Q2, STATE)),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, List.of(
                    P1, Q1, P2, Q2, STATE,
                    new VarMapping("transformer_ISide1", "i1"),
                    new VarMapping("transformer_ISide2", "i2")))
    );

    protected TransformerFixedRatio(TwoWindingsTransformer transformer, String parameterSetId, ModelConfig modelConfig) {
        super(transformer, parameterSetId, modelConfig);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName(side)));
    }

    private String getTerminalVarName(TwoSides side) {
        return "transformer_terminal" + side.getNum();
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VersionableHandler.getInstance().getCurrentVarMapping(VERSIONABLE_VAR_MAPPING);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        equipment.getTerminals().forEach(t -> adder.createTerminalMacroConnections(this, t, this::getVarConnectionsWith, equipment.getSide(t)));
    }

    @Override
    public String getStateValueVarName() {
        return VersionableHandler.getInstance().getCurrentVariable(VARIABLE_VALUE, "transformer_state");
    }

    @Override
    public String getStepVarName() {
        return "transformer_step";
    }

    @Override
    public String getIMonitoredVarName() {
        return "transformer_i1";
    }

    @Override
    public String getPMonitoredVarName() {
        return "transformer_P1";
    }

    @Override
    public String getDisableInternalTapChangerVarName() {
        return "transformer_disable_internal_tapChanger";
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(HIGH_VOLTAGE),
                VersionableHandler.getInstance().getCurrentVariable(VARIABLE_VALUE, "transformer_TAP_CHANGER_locked")));
    }

    @Override
    public String getIVarName(TwoSides side) {
        return "transformer_i" + SideUtils.getSideSuffix(side);
    }

    @Override
    public String getStateVarName() {
        return "transformer_state";
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        return "transformer_desactivate_currentLimits";
    }

    @Override
    public String getUPuVarName(TwoSides sides) {
        return VersionableHandler.getInstance().getCurrentVariable(VARIABLE_VALUE, "@STATIC_ID@@NODE" + sides.getNum() + "@_Upu");
    }

    @Override
    public boolean needMandatoryDynamicModels() {
        return true;
    }
}

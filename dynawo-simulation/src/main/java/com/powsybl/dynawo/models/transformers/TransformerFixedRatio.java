/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.transformers;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatio extends AbstractEquipmentBlackBoxModel<TwoWindingsTransformer> implements TransformerModel, TapChangerModel {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("transformer_P1Pu", "p1"),
            new VarMapping("transformer_Q1Pu", "q1"),
            new VarMapping("transformer_P2Pu", "p2"),
            new VarMapping("transformer_Q2Pu", "q2"),
            new VarMapping("transformer_state", "state"));

    protected TransformerFixedRatio(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, transformer, modelConfig);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName(side)));
    }

    private String getTerminalVarName(TwoSides side) {
        return "transformer_terminal" + side.getNum();
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        equipment.getTerminals().forEach(t -> adder.createTerminalMacroConnections(this, t, this::getVarConnectionsWith, equipment.getSide(t)));
    }

    @Override
    public String getStateValueVarName() {
        return "transformer_state_value";
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
        return List.of(new VarConnection(getTapChangerBlockingVarName(NONE), "transformer_TAP_CHANGER_locked_value"));
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
}

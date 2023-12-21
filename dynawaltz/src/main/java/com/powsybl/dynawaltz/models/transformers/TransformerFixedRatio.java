/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;

import static com.powsybl.dynawaltz.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatio extends AbstractEquipmentBlackBoxModel<TwoWindingsTransformer> implements TransformerModel, TapChangerModel {

    public TransformerFixedRatio(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, transformer, lib);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(TwoSides side) {
        return "transformer_terminal" + side.getNum();
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

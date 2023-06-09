/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawaltz.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TransformerFixedRatio extends AbstractEquipmentBlackBoxModel<TwoWindingsTransformer> implements TransformerModel, TapChangerModel {

    private final String transformerLib;

    public TransformerFixedRatio(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, transformer);
        this.transformerLib = Objects.requireNonNull(lib);
    }

    @Override
    public String getLib() {
        return transformerLib;
    }

    private List<VarConnection> getVarConnectionsWith(BusModel connected, Side side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(Side side) {
        return "transformer_terminal" + side.getSideNumber();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        equipment.getTerminals().forEach(t -> {
            String busStaticId = BusUtils.getConnectableBusStaticId(t);
            createMacroConnections(busStaticId, BusModel.class, this::getVarConnectionsWith, context, SideConverter.convert(equipment.getSide(t)));
        });
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
    public String getIVarName(Side side) {
        return "transformer_i" + side.getSideSuffix();
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

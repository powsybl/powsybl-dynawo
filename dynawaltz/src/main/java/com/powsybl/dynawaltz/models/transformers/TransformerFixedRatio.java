/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TransformerFixedRatio extends AbstractBlackBoxModel implements TransformerModel, TapChangerModel {

    private final String transformerLib;

    public TransformerFixedRatio(String dynamicModelId, String staticId, String parameterSetId, String lib) {
        super(dynamicModelId, staticId, parameterSetId);
        this.transformerLib = lib;
    }

    @Override
    public String getLib() {
        return transformerLib;
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected, Side side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(Side side) {
        return "transformer_terminal" + side.getSideNumber();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        String staticId = getStaticId().orElse(null);
        TwoWindingsTransformer transformer = context.getNetwork().getTwoWindingsTransformer(staticId);
        if (transformer == null) {
            throw new PowsyblException("Transformer static id unknown: " + staticId);
        }
        transformer.getTerminals().forEach(t -> {
            String busStaticId = BusUtils.getConnectableBusStaticId(t);
            createMacroConnections(busStaticId, BusModel.class, this::getVarConnectionsWithBus, context, SideConverter.convert(transformer.getSide(t)));
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

    //TODO check correct value
    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(TAP_CHANGER_BLOCKING_BLOCKED_T, "transformer_TAP_CHANGER_locked_value"));
    }

    @Override
    public String getIVarName(Side side) {
        return null;
    }

    @Override
    public String getStateVarName() {
        return null;
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        return null;
    }
}

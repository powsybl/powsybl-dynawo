/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.loads.LoadWithTransformers;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TapChangerAutomaton extends AbstractPureDynamicBlackBoxModel implements TapChangerModel {

    private final Load load;
    private final TransformerSide side;

    public TapChangerAutomaton(String dynamicModelId, String parameterSetId, Load load, TransformerSide side) {
        super(dynamicModelId, parameterSetId);
        this.load = Objects.requireNonNull(load);
        this.side = Objects.requireNonNull(side);
    }

    public TapChangerAutomaton(String dynamicModelId, String parameterSetId, Load load) {
        this(dynamicModelId, parameterSetId, load, TransformerSide.NONE);
    }

    @Override
    public String getName() {
        return getLib() + side.getSideSuffix();
    }

    @Override
    public String getLib() {
        return "TapChangerAutomaton";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(load, LoadWithTransformers.class, this::getVarConnectionsWithLoadWithTransformers, context);
    }

    private List<VarConnection> getVarConnectionsWithLoadWithTransformers(LoadWithTransformers connected) {
        return connected.getTapChangerVarConnections(side);
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        switch (side) {
            case NONE:
            case HIGH_VOLTAGE:
                return List.of(new VarConnection(TAP_CHANGER_BLOCKING_BLOCKED_T, "tapChanger_locked"));
            case LOW_VOLTAGE:
                return List.of(new VarConnection(TAP_CHANGER_BLOCKING_BLOCKED_D, "tapChanger_locked"));
            default:
                throw new PowsyblException("Transformer side not initialized");
        }
    }
}

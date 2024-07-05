/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.transformers;

import com.powsybl.dynawo.models.defaultmodels.AbstractDefaultModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DefaultTransformer extends AbstractDefaultModel implements TransformerModel, TapChangerModel {

    public DefaultTransformer(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultTransformer";
    }

    @Override
    public String getStateValueVarName() {
        return "@NAME@_state_value";
    }

    @Override
    public String getStepVarName() {
        return "@NAME@_step";
    }

    @Override
    public String getIMonitoredVarName() {
        return "@NAME@_i1";
    }

    @Override
    public String getPMonitoredVarName() {
        return "@NAME@_P1";
    }

    @Override
    public String getDisableInternalTapChangerVarName() {
        return "@NAME@_disable_internal_tapChanger";
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(NONE), "@NAME@_TAP_CHANGER_locked_value"));
    }

    @Override
    public String getIVarName(TwoSides side) {
        return "@NAME@_i" + SideUtils.getSideSuffix(side);
    }

    @Override
    public String getStateVarName() {
        return "@NAME@_state";
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        return "@NAME@_desactivate_currentLimits";
    }
}

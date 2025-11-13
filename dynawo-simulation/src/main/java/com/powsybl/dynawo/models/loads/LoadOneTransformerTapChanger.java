/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformerTapChanger extends LoadOneTransformer implements TapChangerModel {

    protected LoadOneTransformerTapChanger(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        throw new PowsyblException("LoadOneTransformerTapChanger already have a tap changer");
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(NONE), "transformer_tapChanger_locked"));
    }
}

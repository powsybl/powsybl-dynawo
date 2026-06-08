/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.iidm.network.Load;

import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformer extends AbstractLoadOneTransformer implements LoadWithTransformerModel {

    protected LoadOneTransformer(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections() {
        return List.of(new VarConnection("tapChanger_tap", "transformer_tap"),
                new VarConnection("tapChanger_UMonitored", "transformer_U2Pu_value"),
                new VarConnection("tapChanger_switchOffSignal1", "transformer_switchOffSignal1"));
    }
}

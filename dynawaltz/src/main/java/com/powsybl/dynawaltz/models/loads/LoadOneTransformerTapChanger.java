/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;

import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadOneTransformerTapChanger extends LoadOneTransformer implements TapChangerModel {

    //TODO see varmapping & var connections

    public LoadOneTransformerTapChanger(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadOneTransformerTapChanger";
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(TAP_CHANGER_BLOCKING_BLOCKED_T, "tapChanger_locked"));
    }
}

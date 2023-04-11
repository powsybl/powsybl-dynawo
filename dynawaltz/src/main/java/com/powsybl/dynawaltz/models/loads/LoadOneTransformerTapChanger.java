/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
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
    protected List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        List<VarConnection> varConnections = super.getVarConnectionsWithBus(connected);
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection("tapChanger_switchOffSignal1", switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return getTapChangerVarConnections();
    }
}

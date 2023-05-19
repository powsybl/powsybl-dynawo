/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.dynawaltz.models.TransformerSide.HIGH_VOLTAGE;
import static com.powsybl.dynawaltz.models.TransformerSide.LOW_VOLTAGE;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadTwoTransformersTapChangers extends LoadTwoTransformers implements TapChangerModel {

    public LoadTwoTransformersTapChangers(String dynamicModelId, Load load, String parameterSetId) {
        super(dynamicModelId, load, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadTwoTransformersTapChangers";
    }

    @Override
    protected List<VarConnection> getVarConnectionsWith(BusModel connected) {
        List<VarConnection> varConnections = super.getVarConnectionsWith(connected);
        connected.getSwitchOffSignalVarName()
                .ifPresent(switchOff -> {
                    varConnections.add(new VarConnection(getSwitchOffSignal(HIGH_VOLTAGE), switchOff));
                    varConnections.add(new VarConnection(getSwitchOffSignal(LOW_VOLTAGE), switchOff));
                });
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        throw new PowsyblException("LoadTwoTransformersTapChangers already have a tap changer");
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(getTapChangerBlockerVarConnection(LOW_VOLTAGE),
                getTapChangerBlockerVarConnection(HIGH_VOLTAGE));
    }

    private VarConnection getTapChangerBlockerVarConnection(TransformerSide side) {
        return new VarConnection(getTapChangerBlockingVarName(side), "tapChanger" + side.getSideSuffix() + "_locked");
    }

    private String getSwitchOffSignal(TransformerSide side) {
        return "tapChanger" + side.getSideSuffix() + "_switchOffSignal1";
    }
}

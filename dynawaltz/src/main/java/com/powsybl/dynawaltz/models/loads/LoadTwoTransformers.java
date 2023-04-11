/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadTwoTransformers extends AbstractLoad {

    //TODO see varmapping & var connections
    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("transformerT_P1Pu_value", "p"),
            new VarMapping("transformerT_Q1Pu_value", "q"),
            new VarMapping("transformerT_state", "state"));

    public LoadTwoTransformers(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadTwoTransformers";
    }

    @Override
    protected List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        List<VarConnection> varConnections = new ArrayList<>(3);
        varConnections.add(new VarConnection("transformerT_terminal", connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .ifPresent(switchOff -> {
                    varConnections.add(new VarConnection("transformerT_switchOffSignal1", switchOff));
                    varConnections.add(new VarConnection("transformerD_switchOffSignal1", switchOff));
                    varConnections.add(new VarConnection("load_switchOffSignal1", switchOff));
                });
        return varConnections;
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }
}

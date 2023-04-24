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
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.iidm.network.Load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadTwoTransformers extends AbstractLoad implements LoadWithTransformers {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("transformerT_P1Pu_value", "p"),
            new VarMapping("transformerT_Q1Pu_value", "q"),
            new VarMapping("transformerT_state", "state"));

    public LoadTwoTransformers(String dynamicModelId, Load load, String parameterSetId) {
        super(dynamicModelId, load, parameterSetId, "transformer_terminal");
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
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        if (TransformerSide.NONE == side) {
            throw new PowsyblException("LoadTwoTransformers must have a side connected to the Tap changer automaton");
        }
        String transformerPrefix = "transformer" + side.getSideSuffix();
        return List.of(new VarConnection("tapChanger_tap", transformerPrefix + "_tap"),
                new VarConnection("tapChanger_UMonitored", transformerPrefix + "_U2Pu"),
                new VarConnection("tapChanger_switchOffSignal1", transformerPrefix + "_switchOffSignal1"));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }
}

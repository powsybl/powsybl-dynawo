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
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.Load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformers extends AbstractLoad implements LoadWithTransformers {

    public static final String SWITCH_OFF_SIGNAL_NAME = "switchOffSignal1";
    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping(getTransformerVar(HIGH_VOLTAGE, "P1Pu_value"), "p"),
            new VarMapping(getTransformerVar(HIGH_VOLTAGE, "Q1Pu_value"), "q"),
            new VarMapping(getTransformerVar(HIGH_VOLTAGE, "state"), "state"));

    protected LoadTwoTransformers(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig, getTransformerVar(HIGH_VOLTAGE, "terminal1"));
    }

    @Override
    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(3);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .ifPresent(switchOff -> {
                    varConnections.add(new VarConnection(getTransformerVar(HIGH_VOLTAGE, SWITCH_OFF_SIGNAL_NAME), switchOff));
                    varConnections.add(new VarConnection(getTransformerVar(LOW_VOLTAGE, SWITCH_OFF_SIGNAL_NAME), switchOff));
                    varConnections.add(new VarConnection("load_switchOffSignal1", switchOff));
                });
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        if (NONE == side) {
            throw new PowsyblException("LoadTwoTransformers must have a side connected to the Tap changer automaton");
        }
        return List.of(new VarConnection("tapChanger_tap", getTransformerVar(side, "tap")),
                new VarConnection("tapChanger_UMonitored", getTransformerVar(side, "U2Pu")),
                new VarConnection("tapChanger_switchOffSignal1", getTransformerVar(side, SWITCH_OFF_SIGNAL_NAME)));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    private static String getTransformerVar(TransformerSide side, String suffix) {
        return "transformer" + side.getSideSuffix() + "_" + suffix;
    }
}

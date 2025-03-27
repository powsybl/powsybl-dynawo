/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformer extends AbstractLoad implements LoadWithTransformers {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("transformer_P1Pu_value", "p"),
            new VarMapping("transformer_Q1Pu_value", "q"),
            new VarMapping("transformer_state", "state"));

    protected LoadOneTransformer(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig, "transformer_terminal1");
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(3);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .ifPresent(switchOff -> {
                    varConnections.add(new VarConnection("transformer_switchOffSignal1", switchOff));
                    varConnections.add(new VarConnection("load_switchOffSignal1", switchOff));
                });
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        if (NONE != side) {
            throw new PowsyblException("LoadOneTransformer doesn't have a transformer side");
        }
        return List.of(new VarConnection("tapChanger_tap", "transformer_tap"),
                new VarConnection("tapChanger_UMonitored", "transformer_U2Pu_value"),
                new VarConnection("tapChanger_switchOffSignal1", "transformer_switchOffSignal1"));
    }
}

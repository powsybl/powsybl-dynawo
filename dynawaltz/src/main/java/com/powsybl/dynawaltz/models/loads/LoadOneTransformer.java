/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawaltz.models.transformers.TapChangerModel.TAP_CHANGER_BLOCKING_BLOCKED_T;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadOneTransformer extends AbstractLoad implements LoadWithTransformers {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("transformer_P1Pu_value", "p"),
            new VarMapping("transformer_Q1Pu_value", "q"),
            new VarMapping("transformer_state", "state"));

    public LoadOneTransformer(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadOneTransformer";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    private String getTerminalVarName() {
        return "transformer_terminal";
    }

    @Override
    protected List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
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
    public List<VarConnection> getTapChangerVarConnections() {
        return List.of(new VarConnection(TAP_CHANGER_BLOCKING_BLOCKED_T, "tapChanger_locked"));
    }
}

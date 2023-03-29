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
import com.powsybl.iidm.network.Load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadAlphaBeta extends AbstractLoad {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("load_PPu", "p"),
            new VarMapping("load_QPu", "q"),
            new VarMapping("load_state", "state"));

    public LoadAlphaBeta(String dynamicModelId, Load load, String parameterSetId) {
        super(dynamicModelId, load, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    private String getTerminalVarName() {
        return "load_terminal";
    }

    @Override
    protected List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection("load_switchOffSignal1", switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }
}

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
import com.powsybl.dynawaltz.models.buses.StandardBus;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class LoadOneTransformer extends AbstractLoad {

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

    @Override
    protected List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        VarConnection terminalsConnection = new VarConnection("transformer_terminal", connected.getTerminalVarName());
        if (connected instanceof StandardBus) {
            return List.of(terminalsConnection);
        } else {
            VarConnection tSwitchOffConnection = new VarConnection("transformer_switchOffSignal1", connected.getSwitchOffSignalVarName());
            VarConnection lSwitchOffConnection = new VarConnection("load_switchOffSignal1", connected.getSwitchOffSignalVarName());
            return List.of(terminalsConnection, tSwitchOffConnection, lSwitchOffConnection);
        }
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadOneTransformer extends AbstractLoad {

    protected static final List<Pair<String, String>> VAR_MAPPING = Arrays.asList(
            Pair.of("transformer_P1Pu_value", "p"),
            Pair.of("transformer_Q1Pu_value", "q"),
            Pair.of("transformer_state", "state"));

    public LoadOneTransformer(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadOneTransformer";
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (!(connected instanceof BusModel)) {
            throw new PowsyblException("LoadOneTransformer can only connect to BusModel");
        }
        BusModel connectedBusModel = (BusModel) connected;
        return Arrays.asList(
                new VarConnection("transformer_terminal", connectedBusModel.getTerminalVarName()),
                new VarConnection("transformer_switchOffSignal1", connectedBusModel.getSwitchOffSignalVarName()),
                new VarConnection("load_switchOffSignal1", connectedBusModel.getSwitchOffSignalVarName())
        );
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticref.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network.BusModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadOneTransformer extends AbstractLoadModel {

    public LoadOneTransformer(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId,
                Arrays.asList(
                        Pair.of("transformer_P1Pu_value", "p"),
                        Pair.of("transformer_Q1Pu_value", "q"),
                        Pair.of("transformer_state", "state"))
        );
    }

    @Override
    public String getLib() {
        return "LoadOneTransformer";
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof BusModel)) {
            throw new PowsyblException("LoadModel can only connect to BusModel");
        }
        BusModel connectedBusModel = (BusModel) connected;
        return Arrays.asList(
                Pair.of("transformer_terminal", connectedBusModel.getTerminalVarName()),
                Pair.of("transformer_switchOffSignal1", connectedBusModel.getSwitchOffSignalVarName()),
                Pair.of("load_switchOffSignal1", connectedBusModel.getSwitchOffSignalVarName())
        );
    }
}
/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network.LineModel;
import com.powsybl.iidm.network.Branch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class EventQuadripoleDisconnection extends AbstractBlackBoxEventModel {

    public EventQuadripoleDisconnection(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof LineModel)) {
            throw new PowsyblException("EventQuadripoleDisconnection can only connect to LineModel");
        }
        return List.of(Pair.of("event_state1_value", ((LineModel) connected).getStateValueVarName()));
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        BlackBoxModel connectedBbm = getStaticIdLineModelMap(context).get(getStaticId());
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultLineModel(getStaticId(), Branch.Side.ONE));
        }
        return List.of(connectedBbm);
    }

    private Map<String, BlackBoxModelWithStaticId> getStaticIdLineModelMap(DynaWaltzContext dynaWaltzContext) {
        return dynaWaltzContext.getStaticIdBlackBoxModelMap().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof LineModel)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue)
                );
    }

    public String getLineStaticId() {
        return getStaticId();
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.iidm.network.Branch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class EventQuadripoleDisconnection extends AbstractPureDynamicBlackBoxModel {

    private final String lineStaticId;

    public EventQuadripoleDisconnection(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, parameterSetId);
        this.lineStaticId = staticId;
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (!(connected instanceof LineModel)) {
            throw new PowsyblException("EventQuadripoleDisconnection can only connect to LineModel");
        }
        return List.of(new VarConnection("event_state1_value", ((LineModel) connected).getStateValueVarName()));
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(lineStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultLineModel(lineStaticId, Branch.Side.ONE));
        }
        return List.of(connectedBbm);
    }

    public String getLineStaticId() {
        return lineStaticId;
    }
}

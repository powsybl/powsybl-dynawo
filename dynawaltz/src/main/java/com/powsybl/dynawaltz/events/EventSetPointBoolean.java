/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.GeneratorModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
public class EventSetPointBoolean extends AbstractBlackBoxEventModel {

    private final String generatorStaticId;

    public EventSetPointBoolean(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, "", parameterSetId);
        this.generatorStaticId = staticId;
    }

    @Override
    public String getLib() {
        return "EventSetPointBoolean";
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof GeneratorModel)) {
            throw new PowsyblException("EventSetPointBoolean can only connect to GeneratorModel");
        }
        return List.of(Pair.of("event_state1", ((GeneratorModel) connected).getSwitchOffSignalEventVarName()));
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(generatorStaticId);
        if (connectedBbm == null) {
            throw new PowsyblException("Cannot find generator '" + generatorStaticId + "' among the dynamic models provided");
        }
        return List.of(connectedBbm);
    }
}
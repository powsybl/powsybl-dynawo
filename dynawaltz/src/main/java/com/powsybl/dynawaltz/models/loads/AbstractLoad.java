/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractLoad extends AbstractBlackBoxModel {

    protected AbstractLoad(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, Objects.requireNonNull(staticId), parameterSetId);
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        String staticId = getStaticId().orElse(null); // cannot be empty as checked in constructor
        Load load = context.getNetwork().getLoad(staticId);
        if (load == null) {
            throw new PowsyblException("Load static id unknown: " + staticId);
        }
        String connectedStaticId = load.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultBusModel(connectedStaticId));
        }
        return List.of(connectedBbm);
    }
}
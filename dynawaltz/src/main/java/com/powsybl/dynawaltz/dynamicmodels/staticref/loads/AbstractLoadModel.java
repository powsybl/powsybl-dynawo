/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticref.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.staticref.AbstractBlackBoxModelWithStaticRef;
import com.powsybl.iidm.network.Load;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractLoadModel extends AbstractBlackBoxModelWithStaticRef {

    protected AbstractLoadModel(String dynamicModelId, String staticId, String parameterSetId, List<Pair<String, String>> staticRef) {
        super(dynamicModelId, staticId, parameterSetId, staticRef);
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        Load load = context.getNetwork().getLoad(getStaticId());
        if (load == null) {
            throw new PowsyblException("Load static id unknown: " + getStaticId());
        }
        String connectedStaticId = load.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultBusModel(connectedStaticId));
        }
        return List.of(connectedBbm);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writeBlackBoxModel(writer, context);
    }
}
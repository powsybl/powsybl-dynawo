/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.automatons;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.*;
import com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network.LineModel;
import com.powsybl.iidm.network.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurrentLimitAutomaton extends AbstractBlackBoxModel {

    private final Branch.Side side;
    private final String lineStaticId;

    public CurrentLimitAutomaton(String dynamicModelId, String staticId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, "", parameterSetId);
        this.side = Objects.requireNonNull(side);
        this.lineStaticId = staticId;
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        Line line = context.getNetwork().getLine(lineStaticId);
        if (line == null) {
            throw new PowsyblException("Unknown line static id: " + lineStaticId);
        }
        String connectedStaticId = line.getTerminal(side).getBusBreakerView().getConnectableBus().getId();
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultLineModel(lineStaticId, side));
        }
        return List.of(connectedBbm);
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof LineModel)) {
            throw new PowsyblException("CurrentLimitAutomaton can only connect to LineModel");
        }
        LineModel connectedLineModel = (LineModel) connected;
        return Arrays.asList(
                Pair.of("currentLimitAutomaton_IMonitored", connectedLineModel.getIVarName()),
                Pair.of("currentLimitAutomaton_order", connectedLineModel.getStateVarName()),
                Pair.of("currentLimitAutomaton_AutomatonExists", connectedLineModel.getDesactivateCurrentLimitsVarName())
        );
    }

    public String getLineStaticId() {
        return lineStaticId;
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writeAutomatonBlackBoxModel(writer, context);
    }
}

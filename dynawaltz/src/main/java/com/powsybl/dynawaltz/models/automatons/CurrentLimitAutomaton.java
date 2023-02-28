/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.utils.LineSideUtils;
import com.powsybl.iidm.network.Branch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurrentLimitAutomaton extends AbstractPureDynamicBlackBoxModel {

    private final Branch.Side side;
    private final String lineStaticId;

    public CurrentLimitAutomaton(String dynamicModelId, String staticId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, parameterSetId);
        this.side = Objects.requireNonNull(side);
        this.lineStaticId = staticId;
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public String getName() {
        return getLib() + LineSideUtils.getSuffix(side);
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        return List.of(context.getDynamicModelOrDefaultLine(lineStaticId));
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (!(connected instanceof LineModel)) {
            throw new PowsyblException("CurrentLimitAutomaton can only connect to LineModel");
        }
        LineModel connectedLineModel = (LineModel) connected;
        return Arrays.asList(
                new VarConnection("currentLimitAutomaton_IMonitored", connectedLineModel.getIVarName(side)),
                new VarConnection("currentLimitAutomaton_order", connectedLineModel.getStateVarName()),
                new VarConnection("currentLimitAutomaton_AutomatonExists", connectedLineModel.getDesactivateCurrentLimitsVarName())
        );
    }

    public String getLineStaticId() {
        return lineStaticId;
    }
}

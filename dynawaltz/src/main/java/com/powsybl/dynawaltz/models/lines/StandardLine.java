/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.LineSideUtils;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Line;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class StandardLine extends AbstractBlackBoxModel implements LineModel {

    private final Map<String, Branch.Side> busSideConnection = new HashMap<>();

    public StandardLine(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "Line";
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return Arrays.asList(
                new VarConnection(getIVarName(busSideConnection.get(connected.getStaticId().orElseThrow())), connected.getNumCCVarName()),
                new VarConnection(getStateVarName(), connected.getTerminalVarName())
        );
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        String staticId = getStaticId().orElse(null);
        Line line = context.getNetwork().getLine(staticId);
        if (line == null) {
            throw new PowsyblException("Line static id unknown: " + getStaticId());
        }
        line.getTerminals().forEach(t -> {
            String busStaticId = BusUtils.getConnectableBusStaticId(t);
            busSideConnection.put(busStaticId, line.getSide(t));
            createMacroConnections(busStaticId, BusModel.class, false, this::getVarConnectionsWithBus, context);
        });
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public String getIVarName(Branch.Side side) {
        return getDynamicModelId() + LineSideUtils.getSuffix(side);
    }

    @Override
    public String getStateVarName() {
        return getDynamicModelId() + "_state";
    }

    @Override
    public String getDesactivateCurrentLimitsVarName() {
        return getDynamicModelId() + "_desactivate_currentLimits";
    }

    @Override
    public String getStateValueVarName() {
        return getDynamicModelId() + "_state_value";
    }
}

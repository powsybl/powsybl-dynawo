/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.Line;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class StandardLine extends AbstractEquipmentBlackBoxModel<Line> implements LineModel {

    public StandardLine(String dynamicModelId, Line line, String parameterSetId) {
        super(dynamicModelId, parameterSetId, line);
    }

    @Override
    public String getLib() {
        return "Line";
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected, Side side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(Side side) {
        return "line_terminal" + side.getSideNumber();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        equipment.getTerminals().forEach(t -> {
            String busStaticId = BusUtils.getConnectableBusStaticId(t);
            createMacroConnections(busStaticId, BusModel.class, this::getVarConnectionsWithBus, context, SideConverter.convert(equipment.getSide(t)));
        });
    }

    @Override
    public String getName() {
        return getDynamicModelId();
    }

    @Override
    public String getStateVarName() {
        return "line_state";
    }

    @Override
    public String getStateValueVarName() {
        return "line_state_value";
    }

    @Override
    public String getIVarName(Side side) {
        throw new UnsupportedOperationException("i variable not implemented in StandardLine dynawo's model");
    }

    @Override
    public String getDesactivateCurrentLimitsVarName() {
        throw new UnsupportedOperationException("deactivateCurrentLimits variable not implemented in StandardLine dynawo's model");
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.HvdcConverterStation;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcDangling extends BaseHvdc {

    private final TwoSides danglingSide;

    protected HvdcDangling(String dynamicModelId, HvdcLine hvdc, String parameterSetId, ModelConfig modelConfig,
                           HvdcVarNameHandler varNameHandler, TwoSides danglingSide) {
        super(dynamicModelId, hvdc, parameterSetId, modelConfig, varNameHandler);
        this.danglingSide = danglingSide;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWithDangling, danglingSide);
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWith, SideUtils.getOppositeSide(danglingSide));
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        if (danglingSide == side) {
            throw new PowsyblException(String.format("Equipment %s side %s is dangling and can't be disconnected with an event", getLib(), side.getNum()));
        }
        return super.getSwitchOffSignalEventVarName(side);
    }

    @Override
    public List<HvdcConverterStation<?>> getConnectedStations() {
        return List.of(danglingSide == TwoSides.ONE ? equipment.getConverterStation2() : equipment.getConverterStation1());
    }

    private List<VarConnection> getVarConnectionsWithDangling(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(TERMINAL_PREFIX + side.getNum(), connected.getTerminalVarName(side)));
    }
}

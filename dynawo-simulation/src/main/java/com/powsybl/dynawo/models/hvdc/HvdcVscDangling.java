/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.HvdcConverterStation;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVscDangling extends HvdcVsc {

    private final DanglingSide danglingSide;

    protected HvdcVscDangling(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String hvdcLib, TwoSides danglingSide) {
        super(dynamicModelId, hvdc, parameterSetId, hvdcLib);
        this.danglingSide = new DanglingSide(TERMINAL_PREFIX, danglingSide);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        danglingSide.createMacroConnections(
            this::getVarConnectionsWith,
            (varCoSupplier, side) -> adder.createTerminalMacroConnections(this, equipment, varCoSupplier, side)
        );
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        if (danglingSide.isDangling(side)) {
            throw new PowsyblException(String.format("Equipment %s side %s is dangling and can't be disconnected with an event", getLib(), danglingSide.getSideNumber()));
        }
        return super.getSwitchOffSignalEventVarName(side);
    }

    @Override
    public List<HvdcConverterStation<?>> getConnectedStations() {
        return List.of(danglingSide.isDangling(TwoSides.ONE) ? equipment.getConverterStation2() : equipment.getConverterStation1());
    }
}

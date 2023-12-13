/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcPDangling extends HvdcP {

    private final DanglingSide danglingSide;

    public HvdcPDangling(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String hvdcLib, TwoSides danglingSide) {
        super(dynamicModelId, hvdc, parameterSetId, hvdcLib);
        this.danglingSide = new DanglingSide(TERMINAL_PREFIX, danglingSide);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        danglingSide.createMacroConnections(
            this::getVarConnectionsWith,
            (varCoSupplier, side) -> adder.createTerminalMacroConnections(this, equipment.getConverterStation(side).getTerminal(), varCoSupplier, side)
        );
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        if (danglingSide.isDangling(side)) {
            throw new PowsyblException(String.format("Equipment %s side %s is dangling and can't be disconnected with an event", getLib(), danglingSide.getSideNumber()));
        }
        return super.getSwitchOffSignalEventVarName(side);
    }
}

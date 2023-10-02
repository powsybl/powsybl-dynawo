/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.HvdcLine;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class HvdcPDangling extends HvdcP {

    private final DanglingSide danglingSide;

    public HvdcPDangling(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String hvdcLib, Side danglingSide) {
        super(dynamicModelId, hvdc, parameterSetId, hvdcLib);
        this.danglingSide = new DanglingSide(TERMINAL_PREFIX, danglingSide);
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        danglingSide.createMacroConnections(
            this::getVarConnectionsWith,
            (varCoSupplier, side) -> createTerminalMacroConnections(equipment.getConverterStation(SideConverter.convert(side)).getTerminal(), varCoSupplier, context, side)
        );
    }

    @Override
    public String getSwitchOffSignalEventVarName(Side side) {
        if (danglingSide.isDangling(side)) {
            throw new PowsyblException(String.format("Equipment %s side %s is dangling and can't be disconnected with an event", getLib(), danglingSide.getSideNumber()));
        }
        return super.getSwitchOffSignalEventVarName(side);
    }
}

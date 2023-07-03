/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.dynawaltz.models.MacroConnectAttribute;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;

import java.util.List;
import java.util.Optional;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class StaticDefaultBusModel implements ConnectionPoint {

    private static StaticDefaultBusModel INSTANCE;

    private StaticDefaultBusModel() {
    }

    public static StaticDefaultBusModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StaticDefaultBusModel();
        }
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "DefaultBus";
    }

    @Override
    public String getTerminalVarName() {
        return "@STATIC_ID@@NODE@_ACPIN";
    }

    @Override
    public String getTerminalVarName(Side side) {
        return "@STATIC_ID@@NODE" + side.getSideNumber() + "@_ACPIN";
    }

    @Override
    public Optional<String> getSwitchOffSignalVarName() {
        return Optional.of("@STATIC_ID@@NODE@_switchOff");
    }

    @Override
    public Optional<String> getSwitchOffSignalVarName(Side side) {
        return Optional.of("@STATIC_ID@@NODE" + side.getSideNumber() + "@_switchOff");
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(MacroConnectAttribute.of("id2", DynaWaltzXmlConstants.NETWORK));
    }
}

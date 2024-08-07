/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.xml.DynawoSimulationXmlConstants;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;
import java.util.Optional;

/**
 * Used for the connection between an equipment and a bus.
 * Since the equipment static id (@STATIC_ID@ in varname) is the only information needed in order to create var connections,
 * DefaultBusModel is implemented as a singleton
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DefaultEquipmentConnectionPoint implements EquipmentConnectionPoint {

    private static final DefaultEquipmentConnectionPoint INSTANCE = new DefaultEquipmentConnectionPoint();

    private DefaultEquipmentConnectionPoint() {
    }

    public static DefaultEquipmentConnectionPoint getDefaultModel() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "DefaultEquipmentConnectionPoint";
    }

    @Override
    public String getTerminalVarName() {
        return "@STATIC_ID@@NODE@_ACPIN";
    }

    @Override
    public String getTerminalVarName(TwoSides side) {
        return "@STATIC_ID@@NODE" + side.getNum() + "@_ACPIN";
    }

    @Override
    public Optional<String> getSwitchOffSignalVarName() {
        return Optional.of("@STATIC_ID@@NODE@_switchOff");
    }

    @Override
    public Optional<String> getSwitchOffSignalVarName(TwoSides side) {
        return Optional.of("@STATIC_ID@@NODE" + side.getNum() + "@_switchOff");
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(MacroConnectAttribute.of("id2", DynawoSimulationXmlConstants.NETWORK));
    }
}

/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.utils;

import com.powsybl.iidm.network.*;

/**
 * Verifies na equipment terminal(s) are connected and the relates buses are energized and in main connected component
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class EnergizedUtils {

    private EnergizedUtils() {
    }

    public static boolean isEnergized(Injection<?> equipment) {
        return isEnergized(equipment.getTerminal());
    }

    public static boolean isEnergized(Branch<?> equipment) {
        return isEnergized(equipment.getTerminal1()) && isEnergized(equipment.getTerminal2());
    }

    public static boolean isEnergized(Branch<?> equipment, TwoSides side) {
        return TwoSides.ONE == side ? isEnergized(equipment.getTerminal1()) : isEnergized(equipment.getTerminal2());
    }

    public static boolean isEnergized(HvdcLine equipment) {
        return isEnergized(equipment.getConverterStation1().getTerminal())
                && isEnergized(equipment.getConverterStation1().getTerminal());
    }

    public static boolean isEnergized(HvdcLine equipment, TwoSides side) {
        return TwoSides.ONE == side ? isEnergized(equipment.getConverterStation1().getTerminal())
                : isEnergized(equipment.getConverterStation2().getTerminal());
    }

    public static boolean isEnergized(Terminal terminal) {
        if (!terminal.isConnected()) {
            return false;
        }
        return isEnergized(terminal.getBusBreakerView().getBus());
    }

    /**
     * Verifies a bus is energized and in main connected component
     * @param bus the reviewed bus
     * @return <code>true</code> if energized, <code>false</code> if not
     */
    public static boolean isEnergized(Bus bus) {
        return bus != null && !Double.isNaN(bus.getV()) && bus.isInMainConnectedComponent();
    }
}

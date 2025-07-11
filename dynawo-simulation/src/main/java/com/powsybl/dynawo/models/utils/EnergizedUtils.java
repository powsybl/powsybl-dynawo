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
 * Verifies that the equipment terminal(s) are connected and that the related buses are energized and in main connected component
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class EnergizedUtils {

    private EnergizedUtils() {
    }

    public static boolean isEnergizedAndInMainConnectedComponent(Injection<?> equipment) {
        return isEnergizedAndInMainConnectedComponent(equipment.getTerminal());
    }

    public static boolean isEnergizedAndInMainConnectedComponent(Branch<?> equipment) {
        return isEnergizedAndInMainConnectedComponent(equipment.getTerminal1()) && isEnergizedAndInMainConnectedComponent(equipment.getTerminal2());
    }

    public static boolean isEnergizedAndInMainConnectedComponent(Branch<?> equipment, TwoSides side) {
        return TwoSides.ONE == side ? isEnergizedAndInMainConnectedComponent(equipment.getTerminal1()) : isEnergizedAndInMainConnectedComponent(equipment.getTerminal2());
    }

    public static boolean isEnergizedAndInMainConnectedComponent(HvdcLine equipment) {
        return isEnergizedAndInMainConnectedComponent(equipment.getConverterStation1().getTerminal())
                && isEnergizedAndInMainConnectedComponent(equipment.getConverterStation1().getTerminal());
    }

    public static boolean isEnergizedAndInMainConnectedComponent(HvdcLine equipment, TwoSides side) {
        return TwoSides.ONE == side ? isEnergizedAndInMainConnectedComponent(equipment.getConverterStation1().getTerminal())
                : isEnergizedAndInMainConnectedComponent(equipment.getConverterStation2().getTerminal());
    }

    public static boolean isEnergizedAndInMainConnectedComponent(Terminal terminal) {
        if (!terminal.isConnected()) {
            return false;
        }
        return isEnergizedAndInMainConnectedComponent(terminal.getBusBreakerView().getBus());
    }

    /**
     * Verifies a bus is energized and in main connected component
     * @param bus the reviewed bus
     * @return <code>true</code> if energized, <code>false</code> if not
     */
    public static boolean isEnergizedAndInMainConnectedComponent(Bus bus) {
        return bus != null && !Double.isNaN(bus.getV()) && bus.isInMainConnectedComponent();
    }
}

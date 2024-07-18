/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.utils;

import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BusUtils {

    private BusUtils() {
    }

    public static Bus getConnectableBus(Terminal terminal) {
        return terminal.getBusBreakerView().getConnectableBus();
    }

    public static Bus getConnectableBus(Injection<?> injection) {
        return getConnectableBus(injection.getTerminal());
    }
}

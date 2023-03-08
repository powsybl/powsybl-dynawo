/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.utils;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Terminal;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class BusUtils {

    private BusUtils() {
    }

    public static String getConnectableBusStaticId(Terminal terminal) {
        return terminal.getBusBreakerView().getConnectableBus().getId();
    }

    public static String getConnectableBusStaticId(Generator generator) {
        return getConnectableBusStaticId(generator.getTerminal());
    }

    public static String getConnectableBusStaticId(Load load) {
        return getConnectableBusStaticId(load.getTerminal());
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders


import com.powsybl.iidm.network.IdentifiableType

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
final class BuildersUtil {

    private BuildersUtil() {
    }

    private static final EnumSet<IdentifiableType> ACTION_CONNECTION_POINTS = EnumSet.of(IdentifiableType.BUS, IdentifiableType.BUSBAR_SECTION)

    static boolean isActionConnectionPoint(IdentifiableType type) {
        return ACTION_CONNECTION_POINTS.contains(type)
    }
}
/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.iidm.network.*;

import static com.powsybl.dynawo.models.utils.EnergizedUtils.isEnergized;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuildersUtil {

    public static final String MEASUREMENT_POINT_TYPE = IdentifiableType.BUS + "/" + IdentifiableType.BUSBAR_SECTION;

    private BuildersUtil() {
    }

    /**
     * Returns the ActionConnectionPoint (bus or busbar section) identified by the staticId parameter
     * Verifies the point is energized and in main connected component, if not returns null
     * @param network the network containing the ActionConnectionPoint
     * @param staticId the identifiable id
     * @return the energized action connection point if found, <code>null</code> instead
     */
    public static Identifiable<?> getActionConnectionPoint(Network network, String staticId) {
        BusbarSection busbarSection = network.getBusbarSection(staticId);
        if (busbarSection != null) {
            return isEnergized(busbarSection) ? busbarSection : null;
        }
        Bus bus = network.getBusBreakerView().getBus(staticId);
        return isEnergized(bus) ? bus : null;
    }
}

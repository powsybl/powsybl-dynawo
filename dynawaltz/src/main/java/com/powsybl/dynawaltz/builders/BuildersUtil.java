/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuildersUtil {

    public static final String MEASUREMENT_POINT_TYPE = IdentifiableType.BUS + "/" + IdentifiableType.BUSBAR_SECTION;

    private BuildersUtil() {
    }

    /**
     * Returns the ActionConnectionPoint (bus or bus bar section) identified by the staticId parameter
     * Verifies the point is properly energized, if not returns null
     * @param network the network containing the ActionConnectionPoint
     * @param staticId the identifiable id
     * @return the energized action connection point if found, <code>null</code> instead
     */
    public static Identifiable<?> getActionConnectionPoint(Network network, String staticId) {
        BusbarSection busbarSection = network.getBusbarSection(staticId);
        return busbarSection != null && !Double.isNaN(busbarSection.getV())
                ? busbarSection
                : getEnergizedBus(network.getBusBreakerView().getBus(staticId));
    }

    /**
     * Verifies and returns the bus if it is properly energized, if not returns null
     * @param bus the reviewed bus
     * @return the energized bus if found, <code>null</code> instead
     */
    private static Bus getEnergizedBus(Bus bus) {
        return bus != null && bus.isInMainConnectedComponent() && !Double.isNaN(bus.getV()) ? bus : null;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.iidm.network.*;

import static com.powsybl.iidm.network.IdentifiableType.BUS;
import static com.powsybl.iidm.network.IdentifiableType.BUSBAR_SECTION;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuildersUtil {

    public static final String MEASUREMENT_POINT_TYPE = BUS + "/" + BUSBAR_SECTION;

    private BuildersUtil() {
    }

    /**
     * Returns the ActionConnectionPoint (bus or busbar section) identified by the staticId parameter
     * @param network the network containing the ActionConnectionPoint
     * @param staticId the identifiable id
     * @return the energized action connection point if found, <code>null</code> instead
     */
    public static Identifiable<?> getActionConnectionPoint(Network network, String staticId) {
        BusbarSection busbarSection = network.getBusbarSection(staticId);
        return busbarSection != null ? busbarSection : network.getBusBreakerView().getBus(staticId);
    }

    /**
     * Verifies the point is energized and in main connected component
     */
    public static final BuilderEquipment.EquipmentPredicate<Identifiable<?>> IS_ENERGIZED = (eq, f, r) -> {
        boolean isEnergized = switch (eq.getType()) {
            case BUS -> isEnergizedBus((Bus) eq);
            case BUSBAR_SECTION -> isEnergizedBus(((Injection<?>) eq).getTerminal().getBusBreakerView().getBus());
            //TODO
            default -> throw new UnsupportedOperationException("Only bus and bus bar section are supported");
        };
        if (!isEnergized) {
            BuilderReports.reportNotEnergized(r, f, eq.getId());
        }
        return isEnergized;
    };

    /**
     * Verifies a bus is energized and in main connected component
     * @param bus the reviewed bus
     * @return <code>true</code> if energized, <code>false</code> if not
     */
    private static boolean isEnergizedBus(Bus bus) {
        return bus != null && !Double.isNaN(bus.getV()) && bus.isInMainConnectedComponent();
    }
}

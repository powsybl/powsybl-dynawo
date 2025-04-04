/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.utils.EnergizedUtils;
import com.powsybl.iidm.network.*;

import static com.powsybl.dynawo.models.utils.EnergizedUtils.isEnergizedAndInMainConnectedComponent;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuildersUtil {

    public static final String MEASUREMENT_POINT_TYPE = IdentifiableType.BUS + "/" + IdentifiableType.BUSBAR_SECTION;

    /**
     * Verifies the ActionConnectionPoint (bus or busbar section) is energized and in main connected component
     */
    public static final EquipmentPredicate<Identifiable<?>> IS_ACTION_CONNECTION_POINT_ENERGIZED = (eq, f, r) -> {
        boolean isEnergized = switch (eq.getType()) {
            case BUS -> isEnergizedAndInMainConnectedComponent((Bus) eq);
            case BUSBAR_SECTION -> isEnergizedAndInMainConnectedComponent((BusbarSection) eq);
            default -> throw new UnsupportedOperationException("Only bus and bus bar section are supported");
        };
        if (!isEnergized) {
            BuilderReports.reportNotEnergized(r, f, eq.getId());
        }
        return isEnergized;
    };

    @FunctionalInterface
    public interface EquipmentPredicate<T> {
        boolean test(T equipment, String fieldName, ReportNode reportNode);
    }

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
            return EnergizedUtils.isEnergizedAndInMainConnectedComponent(busbarSection) ? busbarSection : null;
        }
        Bus bus = network.getBusBreakerView().getBus(staticId);
        return isEnergizedAndInMainConnectedComponent(bus) ? bus : null;
    }
}

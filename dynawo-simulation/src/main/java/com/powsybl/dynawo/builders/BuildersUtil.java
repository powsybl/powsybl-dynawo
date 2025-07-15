/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.BusbarSection;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import static com.powsybl.dynawo.models.utils.EnergizedUtils.isEnergizedAndInMainConnectedComponent;
import static com.powsybl.iidm.network.IdentifiableType.BUS;
import static com.powsybl.iidm.network.IdentifiableType.BUSBAR_SECTION;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuildersUtil {

    public static final String MEASUREMENT_POINT_TYPE = BUS + "/" + BUSBAR_SECTION;
    public static final StaticIdListUnknownReportNodeBuilder ENERGIZED_REPORT_NODE_BUILDER = BuilderReports::reportEnergizedStaticIdListUnknown;

    /**
     * Verifies the ActionConnectionPoint (bus or busbar section) is energized and in main connected component
     */
    public static final EquipmentChecker<Identifiable<?>> IS_ACTION_CONNECTION_POINT_ENERGIZED = (equipment, fieldName, reportNode) -> {
        boolean isEnergized = switch (equipment.getType()) {
            case BUS -> isEnergizedAndInMainConnectedComponent((Bus) equipment);
            case BUSBAR_SECTION -> isEnergizedAndInMainConnectedComponent((BusbarSection) equipment);
            default -> throw new UnsupportedOperationException("Only bus and bus bar section are supported");
        };
        if (!isEnergized) {
            BuilderReports.reportNotEnergized(reportNode, fieldName, equipment.getId());
        }
        return isEnergized;
    };

    private BuildersUtil() {
    }

    /**
     * Returns the ActionConnectionPoint (bus or busbar section) identified by the staticId parameter
     * @param network the network containing the ActionConnectionPoint
     * @param staticId the identifiable id
     * @return the action connection point if found, <code>null</code> instead
     */
    public static Identifiable<?> getActionConnectionPoint(Network network, String staticId) {
        BusbarSection busbarSection = network.getBusbarSection(staticId);
        return busbarSection != null ? busbarSection : network.getBusBreakerView().getBus(staticId);
    }
}

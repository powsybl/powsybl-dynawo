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

    public static Identifiable<?> getActionConnectionPoint(Network network, String staticId) {
        BusbarSection busbarSection = network.getBusbarSection(staticId);
        return busbarSection != null && !Double.isNaN(busbarSection.getV())
                ? busbarSection
                : getVoltageOnBus(network.getBusBreakerView().getBus(staticId));
    }

    private static Bus getVoltageOnBus(Bus bus) {
        return bus != null && bus.isInMainConnectedComponent() && !Double.isNaN(bus.getV()) ? bus : null;
    }
}

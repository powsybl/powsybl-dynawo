/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class LfResultsUtils {

    private LfResultsUtils() {
    }

    public static Network createSvcTestCaseWithLFResults() {
        Network network = SvcTestCaseFactory.create();
        network.getBusBreakerView().getBuses().forEach(b -> b.setV(400).setAngle(0));
        return network;
    }

    public static Network createHvdcTestNetworkVscWithLFResults() {
        Network network = HvdcTestNetwork.createVsc();
        network.getBusBreakerView().getBus("B1").setV(400).setAngle(0);
        network.getVoltageLevel("VL2").getBusView().getBuses().forEach(b -> b.setV(400).setAngle(0));
        return network;
    }
}

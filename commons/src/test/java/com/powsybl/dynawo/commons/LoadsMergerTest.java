/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.Network;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class LoadsMergerTest extends AbstractDynawoCommonsTest {

    @Test
    public void multiBusesInVoltageLevel() throws IOException {
        Network network = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network expectedIidm = Network.read("mergedLoadsMultiBusesVl.xiidm", getClass().getResourceAsStream("/mergedLoadsMultiBusesVl.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }
}

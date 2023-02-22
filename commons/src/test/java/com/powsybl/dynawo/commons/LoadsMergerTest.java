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
import java.util.List;

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

    @Test
    public void mergeLoadsPpQp() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, 4.0, 36.0, 4.0),
                new LoadState(10.1, 7.2, 10.3, 7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("mergedLoadsPpQp.xiidm", getClass().getResourceAsStream("/mergedLoadsPpQp.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }

    @Test
    public void mergeLoadsPpQn() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, -4.0, 36.0, -4.0),
                new LoadState(10.1, -7.2, 10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("mergedLoadsPpQn.xiidm", getClass().getResourceAsStream("/mergedLoadsPpQn.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }

    @Test
    public void mergeLoadsPnQn() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, -4.0, -36.0, -4.0),
                new LoadState(-10.1, -7.2, -10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("mergedLoadsPnQn.xiidm", getClass().getResourceAsStream("/mergedLoadsPnQn.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }

    @Test
    public void mergeLoadsPnQp() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, 4.0, -36.0, 4.0),
                new LoadState(-10.1, 7.2, -10.3, 7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("mergedLoadsPnQp.xiidm", getClass().getResourceAsStream("/mergedLoadsPnQp.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }

    @Test
    public void mergeThreeLoadsGroups() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, 4.0, -36.0, 4.0),
                new LoadState(36.1, -4.0, 36.0, -4.0),
                new LoadState(-36.1, -4.0, -36.0, -4.0),
                new LoadState(-10.1, 7.2, -10.3, 7.5),
                new LoadState(10.1, -7.2, 10.3, -7.5),
                new LoadState(-10.1, -7.2, -10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("mergedThreeLoadsGroups.xiidm", getClass().getResourceAsStream("/mergedThreeLoadsGroups.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }

    @Test
    public void nonMergeableLoads() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(6.1, 1.0, 36.0, 2.0),
                new LoadState(-3.3, 3.0, -26.0, 3.0),
                new LoadState(36.1, -1.0, 16.0, -4.0),
                new LoadState(-5.5, -2.0, -46.0, -5.0));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        Network expectedIidm = Network.read("nonMergeableLoads.xiidm", getClass().getResourceAsStream("/nonMergeableLoads.xiidm"));
        compare(expectedIidm, LoadsMerger.mergeLoads(network));
    }
}

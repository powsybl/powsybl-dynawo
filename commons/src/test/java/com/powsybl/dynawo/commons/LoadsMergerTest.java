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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
class LoadsMergerTest extends AbstractDynawoCommonsTest {

    @Test
    void multiBusesInVoltageLevel() throws IOException {
        Network network = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        compare("/mergedLoadsMultiBusesVl.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void mergeLoadsPpQp() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, 4.0, 36.0, 4.0),
                new LoadState(10.1, 7.2, 10.3, 7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/mergedLoadsPpQp.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void mergeLoadsPpQn() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, -4.0, 36.0, -4.0),
                new LoadState(10.1, -7.2, 10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/mergedLoadsPpQn.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void mergeLoadsPnQn() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, -4.0, -36.0, -4.0),
                new LoadState(-10.1, -7.2, -10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/mergedLoadsPnQn.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void mergeLoadsPnQp() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, 4.0, -36.0, 4.0),
                new LoadState(-10.1, 7.2, -10.3, 7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/mergedLoadsPnQp.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void mergeThreeLoadsGroups() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(-36.1, 4.0, -36.0, 4.0),
                new LoadState(36.1, -4.0, 36.0, -4.0),
                new LoadState(-36.1, -4.0, -36.0, -4.0),
                new LoadState(-10.1, 7.2, -10.3, 7.5),
                new LoadState(10.1, -7.2, 10.3, -7.5),
                new LoadState(-10.1, -7.2, -10.3, -7.5));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/mergedThreeLoadsGroups.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void nonMergeableLoads() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(6.1, 1.0, 36.0, 2.0),
                new LoadState(-3.3, 3.0, -26.0, 3.0),
                new LoadState(36.1, -1.0, 16.0, -4.0),
                new LoadState(-5.5, -2.0, -46.0, -5.0));
        Network network = TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates);
        compare("/nonMergeableLoads.xiidm", LoadsMerger.mergeLoads(network));
    }

    @Test
    void nonMergeableFictitiousLoads() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, 4.0, 36.0, 4.0),
                new LoadState(10.1, 7.2, 10.3, 7.5));
        Set<Integer> fictitiousLoadPosition = Set.of(1);
        Network network = LoadsMerger.mergeLoads(TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates, fictitiousLoadPosition));
        compare("/notMergeableFictitiousLoads.xiidm", network);
    }

    @Test
    void partialMergeLoadsPpQp() throws IOException {
        List<LoadState> loadStates = List.of(
                new LoadState(36.1, 4.0, 36.0, 4.0),
                new LoadState(10.1, 7.2, 10.3, 7.5),
                new LoadState(20.0, 10.0, 20.0, 7.0));
        Set<Integer> fictitiousLoadPosition = Set.of(2);
        Network network = LoadsMerger.mergeLoads(TestNetworkFactory.createMultiLoadsBusesNetwork(loadStates, fictitiousLoadPosition));
        compare("/partialMergedLoadsPpQp.xiidm", network);
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.powsybl.dynawo.commons.loadmerge.LoadPowers.*;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class LoadsMerger {

    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";

    private LoadsMerger() {
    }

    public static Network mergeLoads(Network network) throws PowsyblException {
        Network mergedLoadsNetwork = NetworkXml.copy(network);

        List<LoadsMerging> loadsMerging = mergedLoadsNetwork.getBusBreakerView().getBusStream()
                .filter(bus -> bus.getLoadStream().count() > 1)
                .map(LoadsMerger::mergeLoads)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        for (LoadsMerging merging : loadsMerging) {
            merging.loadsToMerge.forEach(Connectable::remove);
            merging.loadAdder.setP0(merging.busState.getP0());
            merging.loadAdder.setQ0(merging.busState.getQ0());
            Load load = merging.loadAdder.add();
            load.getTerminal().setP(merging.busState.getP());
            load.getTerminal().setQ(merging.busState.getQ());
        }
        return mergedLoadsNetwork;
    }

    private static List<LoadsMerging> mergeLoads(Bus bus) {
        List<LoadsMerging> loadsMerging = new ArrayList<>();
        getBusStates(bus).forEach((loadCharge, busState) -> loadsMerging.add(mergeLoads(bus, loadCharge, busState)));
        return loadsMerging;
    }

    private static LoadsMerging mergeLoads(Bus bus, LoadPowers loadCharge, BusState busState) {
        VoltageLevel voltageLevel = bus.getVoltageLevel();
        TopologyKind topologyKind = voltageLevel.getTopologyKind();
        LoadAdder loadAdder = voltageLevel.newLoad();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId() + loadCharge.getMergeLoadSuffixId());
        loadAdder.setLoadType(LoadType.UNDEFINED);

        List<Load> loadsToMerge = bus.getLoadStream().filter(l -> getLoadPowers(l) == loadCharge).collect(Collectors.toList());
        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        } else if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            loadsToMerge.stream().findFirst().ifPresent(l -> loadAdder.setNode(l.getTerminal().getNodeBreakerView().getNode()));
        }

        return new LoadsMerging(loadAdder, loadsToMerge, busState);
    }

    static Map<LoadPowers, BusState> getBusStates(Bus bus) {

        int pPosQPosCount = 0;
        int pPosQNegCount = 0;
        int pNegQPosCount = 0;
        int pNegQNegCount = 0;
        double[] pPosQPos = {0, 0, 0, 0};
        double[] pPosQNeg = {0, 0, 0, 0};
        double[] pNegQPos = {0, 0, 0, 0};
        double[] pNegQNeg = {0, 0, 0, 0};

        Map<LoadPowers, BusState> busStates = new EnumMap<>(LoadPowers.class);

        for (Load load : bus.getLoads()) {
            switch (getLoadPowers(load)) {
                case P_POS_Q_POS:
                    addLoad(pPosQPos, load);
                    pPosQPosCount++;
                    break;
                case P_POS_Q_NEG:
                    addLoad(pPosQNeg, load);
                    pPosQNegCount++;
                    break;
                case P_NEG_Q_POS:
                    addLoad(pNegQPos, load);
                    pNegQPosCount++;
                    break;
                case P_NEG_Q_NEG:
                    addLoad(pNegQNeg, load);
                    pNegQNegCount++;
                    break;
            }
        }

        if (pPosQPosCount > 1) {
            busStates.put(P_POS_Q_POS, BusState.createBusStateFromArray(pPosQPos));
        }
        if (pPosQNegCount > 1) {
            busStates.put(P_POS_Q_NEG, BusState.createBusStateFromArray(pPosQNeg));
        }
        if (pNegQPosCount > 1) {
            busStates.put(P_NEG_Q_POS, BusState.createBusStateFromArray(pNegQPos));
        }
        if (pNegQNegCount > 1) {
            busStates.put(P_NEG_Q_NEG, BusState.createBusStateFromArray(pNegQNeg));
        }

        return busStates;
    }

    private static void addLoad(double[] arr, Load load) {
        arr[0] += load.getTerminal().getP();
        arr[1] += load.getTerminal().getQ();
        arr[2] += load.getP0();
        arr[3] += load.getQ0();
    }

    static LoadPowers getLoadPowers(Load load) {
        if (load.getTerminal().getP() >= 0) {
            return load.getTerminal().getQ() >= 0 ? P_POS_Q_POS : P_POS_Q_NEG;
        } else {
            return load.getTerminal().getQ() >= 0 ? P_NEG_Q_POS : P_NEG_Q_NEG;
        }
    }

    private static class LoadsMerging {
        private final BusState busState;
        private final LoadAdder loadAdder;
        private final Iterable<Load> loadsToMerge;

        public LoadsMerging(LoadAdder loadAdder, Iterable<Load> loadsToMerge, BusState busState) {
            this.loadAdder = loadAdder;
            this.loadsToMerge = loadsToMerge;
            this.busState = busState;
        }
    }
}
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
import java.util.List;
import java.util.stream.Collectors;

import static com.powsybl.dynawo.commons.loadmerge.LoadPowers.*;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class LoadsMerger {

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
        return getLoadsToMergeList(bus).stream()
                .filter(loadsToMerge -> !loadsToMerge.isSingle())
                .map(loadsToMerge -> mergeLoads(bus, loadsToMerge))
                .collect(Collectors.toList());
    }

    private static LoadsMerging mergeLoads(Bus bus, LoadsToMerge loadsToMerge) {
        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId() + loadsToMerge.getLoadPowers().getMergeLoadSuffixId());
        loadAdder.setLoadType(LoadType.UNDEFINED);

        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();
        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        } else if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            loadAdder.setNode(loadsToMerge.getLoads().get(0).getTerminal().getNodeBreakerView().getNode());
        }

        return new LoadsMerging(loadAdder, loadsToMerge.getLoads(), loadsToMerge.getBusState());
    }

    static List<LoadsToMerge> getLoadsToMergeList(Bus bus) {

        double[] pPosQPos = {0, 0, 0, 0};
        double[] pPosQNeg = {0, 0, 0, 0};
        double[] pNegQPos = {0, 0, 0, 0};
        double[] pNegQNeg = {0, 0, 0, 0};
        List<Load> pPosQPosLoads = new ArrayList<>();
        List<Load> pPosQNegLoads = new ArrayList<>();
        List<Load> pNegQPosLoads = new ArrayList<>();
        List<Load> pNegQNegLoads = new ArrayList<>();

        List<LoadsToMerge> loadsToMerge = new ArrayList<>();

        for (Load load : bus.getLoads()) {
            switch (getLoadPowers(load)) {
                case P_POS_Q_POS:
                    addLoad(pPosQPos, pPosQPosLoads, load);
                    break;
                case P_POS_Q_NEG:
                    addLoad(pPosQNeg, pPosQNegLoads, load);
                    break;
                case P_NEG_Q_POS:
                    addLoad(pNegQPos, pNegQPosLoads, load);
                    break;
                case P_NEG_Q_NEG:
                    addLoad(pNegQNeg, pNegQNegLoads, load);
                    break;
            }
        }

        if (!pPosQPosLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_POS_Q_POS, BusState.createBusStateFromArray(pPosQPos), pPosQPosLoads));
        }
        if (!pPosQNegLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_POS_Q_NEG, BusState.createBusStateFromArray(pPosQNeg), pPosQNegLoads));
        }
        if (!pNegQPosLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_NEG_Q_POS, BusState.createBusStateFromArray(pNegQPos), pNegQPosLoads));
        }
        if (!pNegQNegLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_NEG_Q_NEG, BusState.createBusStateFromArray(pNegQNeg), pNegQNegLoads));
        }

        return loadsToMerge;
    }

    private static void addLoad(double[] arr, List<Load> loadList, Load load) {
        loadList.add(load);
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
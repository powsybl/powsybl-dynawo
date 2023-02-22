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

        List<LoadsToMerge> loadsToMergeList = mergedLoadsNetwork.getBusBreakerView().getBusStream()
                .filter(bus -> bus.getLoadStream().count() > 1)
                .map(LoadsMerger::mergeLoads)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        for (LoadsToMerge loadsToMerge : loadsToMergeList) {
            loadsToMerge.getLoads().forEach(Connectable::remove);
            loadsToMerge.getLoadAdder().setP0(loadsToMerge.getMergedState().getP0());
            loadsToMerge.getLoadAdder().setQ0(loadsToMerge.getMergedState().getQ0());
            Load load = loadsToMerge.getLoadAdder().add();
            load.getTerminal().setP(loadsToMerge.getMergedState().getP());
            load.getTerminal().setQ(loadsToMerge.getMergedState().getQ());
        }

        return mergedLoadsNetwork;
    }

    private static List<LoadsToMerge> mergeLoads(Bus bus) {
        return getLoadsToMergeList(bus).stream()
                .filter(loadsToMerge -> !loadsToMerge.isSingle())
                .map(loadsToMerge -> mergeLoads(bus, loadsToMerge))
                .collect(Collectors.toList());
    }

    private static LoadsToMerge mergeLoads(Bus bus, LoadsToMerge loadsToMerge) {
        LoadAdder loadAdder = loadsToMerge.getLoadAdder();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId() + loadsToMerge.getLoadPowers().getMergeLoadSuffixId());
        loadAdder.setLoadType(LoadType.UNDEFINED);

        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();
        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        } else if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            loadAdder.setNode(loadsToMerge.getLoads().get(0).getTerminal().getNodeBreakerView().getNode());
        }

        return loadsToMerge;
    }

    public static List<LoadsToMerge> getLoadsToMergeList(Bus bus) {

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
            loadsToMerge.add(new LoadsToMerge(P_POS_Q_POS, LoadState.createLoadStateFromArray(pPosQPos), pPosQPosLoads, bus.getVoltageLevel()));
        }
        if (!pPosQNegLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_POS_Q_NEG, LoadState.createLoadStateFromArray(pPosQNeg), pPosQNegLoads, bus.getVoltageLevel()));
        }
        if (!pNegQPosLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_NEG_Q_POS, LoadState.createLoadStateFromArray(pNegQPos), pNegQPosLoads, bus.getVoltageLevel()));
        }
        if (!pNegQNegLoads.isEmpty()) {
            loadsToMerge.add(new LoadsToMerge(P_NEG_Q_NEG, LoadState.createLoadStateFromArray(pNegQNeg), pNegQNegLoads, bus.getVoltageLevel()));
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

    public static LoadPowers getLoadPowers(Load load) {
        if (load.getTerminal().getP() >= 0) {
            return load.getTerminal().getQ() >= 0 ? P_POS_Q_POS : P_POS_Q_NEG;
        } else {
            return load.getTerminal().getQ() >= 0 ? P_NEG_Q_POS : P_NEG_Q_NEG;
        }
    }
}

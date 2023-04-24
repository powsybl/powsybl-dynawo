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

import static com.powsybl.dynawo.commons.loadmerge.LoadPowersSigns.*;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
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
            loadsToMerge.getLoadAdder().setP0(loadsToMerge.getMergedP0());
            loadsToMerge.getLoadAdder().setQ0(loadsToMerge.getMergedQ0());
            Load load = loadsToMerge.getLoadAdder().add();
            load.getTerminal().setP(loadsToMerge.getMergedP());
            load.getTerminal().setQ(loadsToMerge.getMergedQ());
        }

        return mergedLoadsNetwork;
    }

    private static List<LoadsToMerge> mergeLoads(Bus bus) {
        return getLoadsToMergeList(bus).stream()
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
        List<LoadsToMerge> loadsToMerge = new ArrayList<>();
        getLoadPowersGrouping(bus).forEach((loadPowersSigns, loads) -> {
            if (loads.size() > 1) {
                loadsToMerge.add(new LoadsToMerge(loadPowersSigns, loads, bus.getVoltageLevel().newLoad()));
            }
        });
        return loadsToMerge;
    }

    public static Map<LoadPowersSigns, List<Load>> getLoadPowersGrouping(Bus bus) {
        EnumMap<LoadPowersSigns, List<Load>> loadsGrouping = new EnumMap<>(LoadPowersSigns.class);
        for (Load load : bus.getLoads()) {
            loadsGrouping.computeIfAbsent(getLoadPowers(load), k -> new ArrayList<>()).add(load);
        }
        return loadsGrouping;
    }

    public static LoadPowersSigns getLoadPowers(Load load) {
        if (load.getP0() >= 0) {
            return load.getQ0() >= 0 ? P_POS_Q_POS : P_POS_Q_NEG;
        } else {
            return load.getQ0() >= 0 ? P_NEG_Q_POS : P_NEG_Q_NEG;
        }
    }
}

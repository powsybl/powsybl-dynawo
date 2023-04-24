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
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.commons.loadmerge.LoadPowersSigns.*;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class LoadsMerger {

    private LoadsMerger() {
    }

    public static Network mergeLoads(Network network) throws PowsyblException {
        Network mergedLoadsNetwork = NetworkXml.copy(network);
        mergedLoadsNetwork.getVoltageLevelStream()
                .map(vl -> vl.getBusBreakerView().getBusStream())
                .forEach(LoadsMerger::mergeLoadsInVoltageLevel);
        return mergedLoadsNetwork;
    }

    private static void mergeLoadsInVoltageLevel(Stream<Bus> busStream) {
        List<LoadsToMerge> loadsToMergeList = busStream.filter(bus -> bus.getLoadStream().count() > 1)
                .flatMap(bus -> getLoadsToMergeList(bus).stream())
                .collect(Collectors.toList());

        for (LoadsToMerge loadsToMerge : loadsToMergeList) {
            loadsToMerge.removeLoads();
            Load load = loadsToMerge.getLoadAdder()
                    .setP0(loadsToMerge.getMergedP0())
                    .setQ0(loadsToMerge.getMergedQ0())
                    .add();
            load.getTerminal().setP(loadsToMerge.getMergedP());
            load.getTerminal().setQ(loadsToMerge.getMergedQ());
        }
    }

    public static List<LoadsToMerge> getLoadsToMergeList(Bus bus) {
        List<LoadsToMerge> loadsToMerge = new ArrayList<>();
        getLoadPowersGrouping(bus).forEach((loadPowersSigns, loads) -> {
            if (loads.size() > 1) {
                loadsToMerge.add(new LoadsToMerge(loadPowersSigns, loads, bus));
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

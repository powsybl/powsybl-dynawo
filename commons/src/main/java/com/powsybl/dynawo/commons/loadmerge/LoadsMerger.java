/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.xml.NetworkXml;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
        mergedLoadsNetwork.getVoltageLevelStream().forEach(LoadsMerger::mergeLoadsInVoltageLevel);
        return mergedLoadsNetwork;
    }

    private static void mergeLoadsInVoltageLevel(VoltageLevel vl) {
        List<LoadsToMerge> loadsToMergeList = vl.getBusBreakerView().getBusStream()
                .filter(bus -> bus.getLoadStream().count() > 1)
                .flatMap(LoadsMerger::getLoadsToMergeStream)
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

    private static Stream<LoadsToMerge> getLoadsToMergeStream(Bus bus) {
        return getLoadPowersSignsGrouping(bus).entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> new LoadsToMerge(e.getKey(), e.getValue(), bus));
    }

    public static Map<LoadPowersSigns, List<Load>> getLoadPowersSignsGrouping(Bus bus) {
        return bus.getLoadStream().collect(Collectors.groupingBy(
                LoadsMerger::getLoadPowersSigns, () -> new EnumMap<>(LoadPowersSigns.class), Collectors.toList()));
    }

    public static LoadPowersSigns getLoadPowersSigns(Load load) {
        if (load.getP0() >= 0) {
            return load.getQ0() >= 0 ? P_POS_Q_POS : P_POS_Q_NEG;
        } else {
            return load.getQ0() >= 0 ? P_NEG_Q_POS : P_NEG_Q_NEG;
        }
    }
}

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
import com.powsybl.iidm.serde.NetworkSerDe;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.commons.loadmerge.LoadPowersSigns.*;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Isertial {@literal <laurent.issertial at rte-france.com>}
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 */
public final class LoadsMerger {

    private LoadsMerger() {
    }

    public static Network mergeLoads(Network network, boolean inplace) throws PowsyblException {
        Network mergedLoadsNetwork = inplace ? network : NetworkSerDe.copy(network);
        mergedLoadsNetwork.getVoltageLevelStream().forEach(LoadsMerger::mergeLoadsInVoltageLevel);
        return mergedLoadsNetwork;
    }

    public static Network mergeLoads(Network network) throws PowsyblException {
        return mergeLoads(network, false);
    }

    private static void mergeLoadsInVoltageLevel(VoltageLevel vl) {
        // we need to build the list of loads to merge beforehand as the buses of this voltage level
        // will be invalidated once a load is removed
        List<LoadsToMerge> loadsToMergeList = vl.getBusBreakerView().getBusStream()
                .filter(bus -> bus.getLoadStream().count() > 1)
                .flatMap(LoadsMerger::getLoadsToMergeStream)
                .toList();
        loadsToMergeList.forEach(LoadsToMerge::merge);
    }

    private static Stream<LoadsToMerge> getLoadsToMergeStream(Bus bus) {
        return bus.getLoadStream()
                .filter(l -> !l.isFictitious())
                .collect(Collectors.groupingBy(
                        LoadsMerger::getLoadPowersSigns,
                        () -> new EnumMap<>(LoadPowersSigns.class),
                        Collectors.toList()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> new LoadsToMerge(e.getKey(), e.getValue(), bus));
    }

    public static Map<LoadPowersSigns, Terminal> getLoadTerminalByPowersSigns(Bus bus) {
        return bus.getLoadStream()
                .filter(l -> !l.isFictitious())
                .collect(Collectors.toMap(
                    LoadsMerger::getLoadPowersSigns,
                    Load::getTerminal,
                    (existing, replacement) -> existing,
                    () -> new EnumMap<>(LoadPowersSigns.class)));
    }

    public static Map<LoadPowersSigns, List<Terminal>> getLoadTerminalsByPowersSigns(Bus bus) {
        return bus.getLoadStream()
                .filter(l -> !l.isFictitious())
                .collect(Collectors.groupingBy(
                    LoadsMerger::getLoadPowersSigns,
                    () -> new EnumMap<>(LoadPowersSigns.class),
                    Collectors.mapping(Load::getTerminal, Collectors.toList())));
    }

    private static LoadPowersSigns getLoadPowersSigns(Load load) {
        if (load.getP0() >= 0) {
            return load.getQ0() >= 0 ? P_POS_Q_POS : P_POS_Q_NEG;
        } else {
            return load.getQ0() >= 0 ? P_NEG_Q_POS : P_NEG_Q_NEG;
        }
    }
}

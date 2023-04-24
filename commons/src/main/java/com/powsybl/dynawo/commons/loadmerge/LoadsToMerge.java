/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

import com.powsybl.iidm.network.*;

import java.util.List;

/**
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 */
public class LoadsToMerge {
    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";
    private final List<Load> loads;
    private final LoadAdder loadAdder;
    private final double mergedP;
    private final double mergedQ;
    private final double mergedP0;
    private final double mergedQ0;

    public LoadsToMerge(LoadPowersSigns loadPowersSigns, List<Load> loads, Bus bus) {
        this.loads = loads;
        this.loadAdder = createLoadAdder(loads, loadPowersSigns, bus);
        this.mergedP = loads.stream().map(Load::getTerminal).mapToDouble(Terminal::getP).sum();
        this.mergedQ = loads.stream().map(Load::getTerminal).mapToDouble(Terminal::getQ).sum();
        this.mergedP0 = loads.stream().mapToDouble(Load::getP0).sum();
        this.mergedQ0 = loads.stream().mapToDouble(Load::getQ0).sum();
    }

    private static LoadAdder createLoadAdder(List<Load> loads, LoadPowersSigns loadPowersSigns, Bus bus) {
        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId() + loadPowersSigns.getMergeLoadSuffixId());
        loadAdder.setLoadType(LoadType.UNDEFINED);

        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();
        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        } else if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            loadAdder.setNode(loads.get(0).getTerminal().getNodeBreakerView().getNode());
        }

        return loadAdder;
    }

    public double getMergedP0() {
        return mergedP0;
    }

    public double getMergedQ0() {
        return mergedQ0;
    }

    public double getMergedP() {
        return mergedP;
    }

    public double getMergedQ() {
        return mergedQ;
    }

    public LoadAdder getLoadAdder() {
        return loadAdder;
    }

    public void removeLoads() {
        loads.forEach(Connectable::remove);
    }
}

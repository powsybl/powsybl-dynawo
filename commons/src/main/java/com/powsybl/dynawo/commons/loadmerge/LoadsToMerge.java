/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.LoadAdder;
import com.powsybl.iidm.network.Terminal;

import java.util.List;

/**
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 */
public class LoadsToMerge {
    private final LoadPowers loadPowers;
    private final List<Load> loads;
    private final LoadAdder loadAdder;
    private final double mergedP;
    private final double mergedQ;
    private final double mergedP0;
    private final double mergedQ0;

    public LoadsToMerge(LoadPowers loadPowers, List<Load> loads, LoadAdder loadAdder) {
        this.loadPowers = loadPowers;
        this.loads = loads;
        this.loadAdder = loadAdder;
        this.mergedP = loads.stream().map(Load::getTerminal).mapToDouble(Terminal::getP).sum();
        this.mergedQ = loads.stream().map(Load::getTerminal).mapToDouble(Terminal::getQ).sum();
        this.mergedP0 = loads.stream().mapToDouble(Load::getP0).sum();
        this.mergedQ0 = loads.stream().mapToDouble(Load::getQ0).sum();
    }

    public LoadPowers getLoadPowers() {
        return loadPowers;
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

    public List<Load> getLoads() {
        return loads;
    }

    public LoadAdder getLoadAdder() {
        return loadAdder;
    }
}

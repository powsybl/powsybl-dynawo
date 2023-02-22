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
import com.powsybl.iidm.network.VoltageLevel;

import java.util.List;

/**
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 */
public class LoadsToMerge {
    private final LoadPowers loadPowers;
    private final LoadState mergedState;
    private final List<Load> loads;
    private final LoadAdder loadAdder;

    public LoadsToMerge(LoadPowers loadPowers, LoadState mergedState, List<Load> loads, VoltageLevel voltageLevel) {
        this.loadPowers = loadPowers;
        this.mergedState = mergedState;
        this.loads = loads;
        this.loadAdder = isSingle() ? null : voltageLevel.newLoad();
    }

    public LoadPowers getLoadPowers() {
        return loadPowers;
    }

    public LoadState getMergedState() {
        return mergedState;
    }

    public List<Load> getLoads() {
        return loads;
    }

    public LoadAdder getLoadAdder() {
        return loadAdder;
    }

    public boolean isSingle() {
        return loads.size() == 1;
    }
}

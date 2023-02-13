/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

import com.powsybl.iidm.network.Load;

import java.util.List;

/**
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 */
class LoadsToMerge {
    private final LoadPowers loadPowers;
    private final BusState busState;
    private final List<Load> loads;

    public LoadsToMerge(LoadPowers loadPowers, BusState busState, List<Load> loads) {
        this.loadPowers = loadPowers;
        this.busState = busState;
        this.loads = loads;
    }

    public LoadPowers getLoadPowers() {
        return loadPowers;
    }

    public BusState getBusState() {
        return busState;
    }

    public List<Load> getLoads() {
        return loads;
    }

    public boolean isSingle() {
        return loads.size() == 1;
    }
}

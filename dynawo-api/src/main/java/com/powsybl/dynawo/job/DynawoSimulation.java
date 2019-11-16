/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulation {

    private final int startTime;
    private final int stopTime;
    private final boolean activeCriteria;

    public DynawoSimulation(int startTime, int stopTime, boolean activateCriteria) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.activeCriteria = activateCriteria;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public boolean isActiveCriteria() {
        return activeCriteria;
    }

}

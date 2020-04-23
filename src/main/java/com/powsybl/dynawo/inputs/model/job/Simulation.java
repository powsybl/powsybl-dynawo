/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.Objects;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Simulation {

    private final int startTime;
    private final int stopTime;

    public Simulation(DynamicSimulationParameters parameters) {
        this(parameters.getStartTime(), parameters.getStopTime());
    }

    public Simulation(int startTime, int stopTime) {
        this.startTime = Objects.requireNonNull(startTime);
        this.stopTime = Objects.requireNonNull(stopTime);
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStopTime() {
        return stopTime;
    }
}

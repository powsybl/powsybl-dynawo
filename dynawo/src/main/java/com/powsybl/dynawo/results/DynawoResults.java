/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.results;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoResults implements DynamicSimulationResult {

    public DynawoResults(boolean ok, String logs) {
        this.ok = ok;
        this.logs = logs;
    }

    @Override
    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    @Override
    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    private boolean ok;
    private String logs;
    private TimeSeries timeSeries;
}

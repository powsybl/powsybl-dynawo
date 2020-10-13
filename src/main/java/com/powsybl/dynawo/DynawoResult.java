/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.csv.CsvCurvesParser;
import com.powsybl.timeseries.TimeSeries;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoResult implements DynamicSimulationResult {

    private final boolean isOk;
    private final String logs;
    private final Map<String, TimeSeries> ts;

    public DynawoResult(Path file) {
        isOk = Files.exists(file);
        logs = null;
        if (isOk) {
            ts = CsvCurvesParser.parseCsv(file);
        } else {
            ts = null;
        }
    }

    @Override
    public boolean isOk() {
        return isOk;
    }

    @Override
    public String getLogs() {
        return logs;
    }

    public Map<String, TimeSeries> getCurves() {
        return ts;
    }

    public TimeSeries getCurve(String curve) {
        return ts.get(curve);
    }
}

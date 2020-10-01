package com.powsybl.dynawo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.timeseries.TimeSeries;

public class DynawoResult implements DynamicSimulationResult {

    private final boolean isOk;
    private final String logs;
    private final Map<Integer, List<TimeSeries>> ts;

    public DynawoResult(Path csv) {
        Objects.requireNonNull(csv);
        isOk = Files.exists(csv)?true:false;
        logs = null;
        ts = TimeSeries.parseCsv(csv);
    }

    @Override
    public boolean isOk() {
        return isOk;
    }

    @Override
    public String getLogs() {
        return logs;
    }

    public Map<Integer, List<TimeSeries>> getTimeSeries() {
        return ts;
    }

}

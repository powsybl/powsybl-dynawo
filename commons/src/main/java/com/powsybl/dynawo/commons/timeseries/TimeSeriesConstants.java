package com.powsybl.dynawo.commons.timeseries;

import java.util.List;

final class TimeSeriesConstants {

    private TimeSeriesConstants() {
    }

    static final String TIME = "time";
    static final String MODEL_NAME = "modelName";
    static final String MESSAGE = "message";
    static final List<String> VALUES = List.of(MODEL_NAME, MESSAGE);
}

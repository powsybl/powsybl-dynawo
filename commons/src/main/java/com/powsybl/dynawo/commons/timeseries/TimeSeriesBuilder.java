package com.powsybl.dynawo.commons.timeseries;

import com.powsybl.timeseries.*;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TimeSeriesBuilder {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");
    private final List<String> times = new ArrayList<>();
    private final List<Pair<String, List<String>>> timeSeriesValues = new ArrayList<>();
    private final int expectedTokens;

    public TimeSeriesBuilder(List<String> names) {
        expectedTokens = names.size() + 1;
        for (String name : names) {
            timeSeriesValues.add(Pair.of(name, new ArrayList<>()));
        }
    }

    public void parseLine(String... parsedValues) {
        times.add(DECIMAL_FORMAT.format(Double.parseDouble(parsedValues[0]) * 1000));
        for (int i = 1; i < parsedValues.length; i++) {
            timeSeriesValues.get(i - 1).getValue().add(parsedValues[i]);
        }
    }

    public Map<String, StringTimeSeries> createTimeSeries() {
        TimeSeriesIndex tsi = new IrregularTimeSeriesIndex(times.stream().mapToLong(Long::parseLong).toArray());
        return timeSeriesValues.stream().collect(Collectors.toMap(
            Pair::getKey,
            tsv -> TimeSeries.createString(tsv.getKey(), tsi, tsv.getValue().toArray(String[]::new))));
    }

    public int linesParsed() {
        return times.size();
    }

    public int getExpectedTokens() {
        return expectedTokens;
    }
}

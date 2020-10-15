/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.timeseries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Doubles;
import com.powsybl.timeseries.DoubleDataChunk;
import com.powsybl.timeseries.IrregularTimeSeriesIndex;
import com.powsybl.timeseries.StoredDoubleTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;
import com.powsybl.timeseries.TimeSeriesIndex;
import com.powsybl.timeseries.TimeSeriesMetadata;
import com.powsybl.timeseries.UncompressedDoubleDataChunk;

import gnu.trove.list.array.TDoubleArrayList;

public class ParsingContext {
    final List<String> names;

    final TimeSeriesDataType[] dataTypes;
    final Object[] values;

    final List<Long> times = new ArrayList<>();

    ParsingContext(List<String> names) {
        this.names = names;
        dataTypes = new TimeSeriesDataType[names.size()];
        values = new Object[names.size()];
    }

    private static TimeSeriesException assertDataType(TimeSeriesDataType dataType) {
        return new TimeSeriesException("Unexpected data type " + dataType);
    }

    private TDoubleArrayList createDoubleValues() {
        TDoubleArrayList doubleValues = new TDoubleArrayList();
        if (!times.isEmpty()) {
            doubleValues.fill(0, times.size(), Double.NaN);
        }
        return doubleValues;
    }

    private double parseDouble(String token) {
        return token.isEmpty() ? Double.NaN : Double.parseDouble(token);
    }

    void parseToken(int i, String token) {
        if (dataTypes[i - 1] == null) {
            // test double parsing, in case of error we consider it a string time series
            if (Doubles.tryParse(token) != null) {
                dataTypes[i - 1] = TimeSeriesDataType.DOUBLE;
                TDoubleArrayList doubleValues = createDoubleValues();
                doubleValues.add(parseDouble(token));
                values[i - 1] = doubleValues;
            }
        } else {
            if (dataTypes[i - 1] == TimeSeriesDataType.DOUBLE) {
                ((TDoubleArrayList) values[i - 1]).add(parseDouble(token));
            } else {
                throw assertDataType(dataTypes[i - 1]);
            }
        }
    }

    void parseLine(List<String> tokens) {
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i) != null ? tokens.get(i).trim() : "";
            parseToken(i, token);
        }

        Double time = Double.parseDouble(tokens.get(0)) * 1000;
        times.add(time.longValue());
    }

    Map<String, TimeSeries> createTimeSeries() {
        TimeSeriesIndex index = new IrregularTimeSeriesIndex(times.stream().mapToLong(l -> l).toArray());

        Map<String, TimeSeries> timeSeries = new HashMap<>(names.size());
        for (int i = 0; i < names.size(); i++) {
            TimeSeriesMetadata metadata = new TimeSeriesMetadata(names.get(i), dataTypes[i], index);
            if (dataTypes[i] == TimeSeriesDataType.DOUBLE) {
                TDoubleArrayList doubleValues = (TDoubleArrayList) values[i];
                DoubleDataChunk chunk = new UncompressedDoubleDataChunk(0, doubleValues.toArray()).tryToCompress();
                timeSeries.put(names.get(i), new StoredDoubleTimeSeries(metadata, chunk));
            } else {
                throw assertDataType(dataTypes[i - 1]);
            }
        }
        return timeSeries;
    }
}

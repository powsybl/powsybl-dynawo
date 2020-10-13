/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Doubles;
import com.powsybl.timeseries.DoubleDataChunk;
import com.powsybl.timeseries.IrregularTimeSeriesIndex;
import com.powsybl.timeseries.StoredDoubleTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;
import com.powsybl.timeseries.TimeSeriesIndex;
import com.powsybl.timeseries.TimeSeriesMetadata;
import com.powsybl.timeseries.UncompressedDoubleDataChunk;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class CsvCurvesParser {

    private CsvCurvesParser() {
    }

    public static Map<String, TimeSeries> parseCsv(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parseCsv(file, TimeSeriesConstants.DEFAULT_SEPARATOR);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Map<String, TimeSeries> parseCsv(Path file, char separator) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parseCsv(reader, separator);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Map<String, TimeSeries> parseCsv(String csv, char separator) {
        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            return parseCsv(reader, separator);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Map<String, TimeSeries> parseCsv(BufferedReader reader, char separator) {
        Objects.requireNonNull(reader);

        Stopwatch stopwatch = Stopwatch.createStarted();

        Map<String, TimeSeries> timeSeries = new HashMap<>();
        String separatorStr = Character.toString(separator);

        try {
            CsvListReader csvListReader = new CsvListReader(reader, new CsvPreference.Builder('"', separator, System.lineSeparator()).build());
            CsvParsingContext context = readCsvHeader(csvListReader, separatorStr);
            readCsvValues(csvListReader, context, timeSeries);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        LoggerFactory.getLogger(TimeSeries.class)
                .info("{} time series loaded from CSV in {} ms",
                timeSeries.size(),
                stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return timeSeries;
    }

    static CsvParsingContext readCsvHeader(CsvListReader csvListReader, String separatorStr) throws IOException {
        String[] tokens = csvListReader.getHeader(true);
        if (tokens == null) {
            throw new TimeSeriesException("CSV header is missing");
        }

        if (tokens.length < 1 || !"time".equals(tokens[0])) {
            throw new TimeSeriesException("Bad CSV header, should be \ntime" + separatorStr + "...");
        }
        List<String> duplicates = new ArrayList<>();
        Set<String> namesWithoutDuplicates = new HashSet<>();
        for (String token : tokens) {
            if (!namesWithoutDuplicates.add(token)) {
                duplicates.add(token);
            }
        }
        if (!duplicates.isEmpty()) {
            throw new TimeSeriesException("Bad CSV header, there are duplicates in time series names " + duplicates);
        }
        List<String> names = Arrays.asList(tokens).subList(1, tokens.length);
        return new CsvParsingContext(names);
    }

    static void readCsvValues(CsvListReader reader, CsvParsingContext context, Map<String, TimeSeries> timeSeries) throws IOException {
        List<String> tokens;
        while ((tokens = reader.read()) != null) {

            if (tokens.size() != context.names.size() + 1) {
                throw new TimeSeriesException("Columns of line " + context.times.size() + " are inconsistent with header");
            }

            context.parseLine(tokens);
        }
        timeSeries.putAll(context.createTimeSeries());
    }

    static double parseDouble(String token) {
        return token.isEmpty() ? Double.NaN : Double.parseDouble(token);
    }

    static class CsvParsingContext {
        final List<String> names;

        final TimeSeriesDataType[] dataTypes;
        final Object[] values;

        final List<Long> times = new ArrayList<>();

        CsvParsingContext(List<String> names) {
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

        void parseToken(int i, String token) {
            if (dataTypes[i - 1] == null) {
                // test double parsing, in case of error we consider it a string time series
                if (Doubles.tryParse(token) != null) {
                    dataTypes[i - 1] = TimeSeriesDataType.DOUBLE;
                    TDoubleArrayList doubleValues = createDoubleValues();
                    doubleValues.add(parseDouble(token));
                    values[i - 1] = doubleValues;
                } else {
                    throw assertDataType(TimeSeriesDataType.DOUBLE);
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
}

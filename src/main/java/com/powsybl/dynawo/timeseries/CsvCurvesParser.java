/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.timeseries;

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
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesException;

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
            ParsingContext context = readCsvHeader(csvListReader, separatorStr);
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

    static ParsingContext readCsvHeader(CsvListReader csvListReader, String separatorStr) throws IOException {
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
        return new ParsingContext(names);
    }

    static void readCsvValues(CsvListReader reader, ParsingContext context, Map<String, TimeSeries> timeSeries) throws IOException {
        List<String> tokens;
        while ((tokens = reader.read()) != null) {

            if (tokens.size() != context.names.size() + 1) {
                throw new TimeSeriesException("Columns of line " + context.times.size() + " are inconsistent with header");
            }

            context.parseLine(tokens);
        }
        timeSeries.putAll(context.createTimeSeries());
    }
}

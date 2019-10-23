/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.results;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamic.simulation.DynamicSimulationResult;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
//TODO pending to use Powsybl TimeSeries
public class DynawoResults implements DynamicSimulationResult {

    public DynawoResults(boolean status, Map<String, String> metrics, String logs) {
        this.status = status;
        this.metrics = Objects.requireNonNull(metrics);
        this.logs = logs;
    }

    @Override
    public boolean isOk() {
        return status;
    }

    @Override
    public Map<String, String> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    @Override
    public String getLogs() {
        return logs;
    }

    public void parseCsv(Path file) {
        try {
            parseCsv(Files.newInputStream(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void parseCsv(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            parseCsv(reader, ';');
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<String> getNames() {
        return names;
    }

    public Map<Double, List<Double>> getTimeSeries() {
        return Collections.unmodifiableMap(timeSeries);
    }

    private void parseCsv(BufferedReader reader, char separator) {
        Objects.requireNonNull(reader);

        String separatorStr = Character.toString(separator);
        try {
            readCsvHeader(reader, separatorStr);
            readCsvValues(reader, separatorStr);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void readCsvHeader(BufferedReader reader, String separatorStr) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new PowsyblException("CSV header is missing");
        }
        String[] tokens = line.split(separatorStr);
        if (tokens.length < 1 || !"time".equals(tokens[0])) {
            throw new PowsyblException("Bad CSV header, should be \ntime" + separatorStr + "...");
        }
        List<String> duplicates = new ArrayList<>();
        Set<String> namesWithoutDuplicates = new HashSet<>();
        for (int i = 0; i < tokens.length; i++) {
            if (!namesWithoutDuplicates.add(tokens[i])) {
                duplicates.add(tokens[i]);
            }
        }
        if (!duplicates.isEmpty()) {
            throw new PowsyblException("Bad CSV header, there are duplicates in time series names " + duplicates);
        }
        names = Arrays.asList(tokens).subList(1, tokens.length);
    }

    private void readCsvValues(BufferedReader reader, String separatorStr) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(separatorStr);

            if (tokens.length != names.size() + 1) {
                throw new PowsyblException("Columns of line " + names.size() + " are inconsistent with header");
            }

            parseLine(tokens);
        }
    }

    private void parseLine(String[] tokens) {
        List<Double> values = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();
            values.add(parseToken(token));
        }
        Double time = parseToken(tokens[0].trim());
        timeSeries.put(time, values);
    }

    private Double parseToken(String token) {
        return Double.valueOf(token);
    }

    private final boolean status;
    private final Map<String, String> metrics;
    private final String logs;
    private List<String> names = new ArrayList<>();
    private Map<Double, List<Double>> timeSeries = new HashMap<>();
}

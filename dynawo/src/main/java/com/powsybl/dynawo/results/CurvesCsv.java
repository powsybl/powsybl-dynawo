/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.results;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import com.powsybl.commons.PowsyblException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class CurvesCsv {

    private static final String SEPARATOR = ";";

    private CurvesCsv() {
    }

    public static TimeSeries parse(Path file) {
        try (InputStream is = Files.newInputStream(file)) {
            return parse(is);
        } catch (IOException e) {
            throw new PowsyblException(e);
        }
    }

    public static TimeSeries parse(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return parse(reader);
        } catch (IOException e) {
            throw new PowsyblException(e);
        }
    }

    private static TimeSeries parse(BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        List<String> names = parseHeader(reader);
        Map<Double, List<Double>> values = parseValues(reader, names);
        return new TimeSeries(names, values);
    }

    private static List<String> parseHeader(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new PowsyblException("CSV header is missing");
        }
        String[] tokens = line.split(SEPARATOR);
        if (tokens.length < 1 || !"time".equals(tokens[0])) {
            throw new PowsyblException("Bad CSV header, should be: time" + SEPARATOR + "...");
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
        return Arrays.asList(tokens).subList(1, tokens.length);
    }

    private static Map<Double, List<Double>> parseValues(BufferedReader reader, List<String> names) throws IOException {
        Map<Double, List<Double>> values = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(SEPARATOR);
            if (tokens.length != names.size() + 1) {
                throw new PowsyblException("Columns of line " + names.size() + " are inconsistent with header");
            }
            parseValues(tokens, values);
        }
        return values;
    }

    private static void parseValues(String[] tokens, Map<Double, List<Double>> values) {
        List<Double> tvalues = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();
            tvalues.add(parseToken(token));
        }
        Double time = parseToken(tokens[0].trim());
        values.put(time, tvalues);
    }

    private static Double parseToken(String token) {
        return Double.valueOf(token);
    }

}

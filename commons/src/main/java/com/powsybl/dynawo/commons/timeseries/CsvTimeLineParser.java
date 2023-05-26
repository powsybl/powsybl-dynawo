/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeseries;

import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesCsvConfig;
import com.powsybl.timeseries.TimeSeriesException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynawo.commons.timeseries.TimeSeriesConstants.VALUES;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class CsvTimeLineParser {

    private CsvTimeLineParser() {
    }

    public static Map<String, StringTimeSeries> parseCsv(Path file) {
        return parseCsv(file, '|');
    }

    public static Map<String, StringTimeSeries> parseCsv(Path file, char separator) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parseCsv(reader, separator);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Map<String, StringTimeSeries> parseCsv(BufferedReader reader, char separator) {
        Objects.requireNonNull(reader);
        TimeSeriesCsvConfig timeSeriesCsvConfig = new TimeSeriesCsvConfig(separator, false, TimeSeries.TimeFormat.DATE_TIME);
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(timeSeriesCsvConfig.separator());
        settings.getFormat().setQuoteEscape('"');
        settings.getFormat().setLineSeparator(System.lineSeparator());
        settings.setMaxColumns(timeSeriesCsvConfig.getMaxColumns());
        CsvParser csvParser = new CsvParser(settings);
        ResultIterator<String[], com.univocity.parsers.common.ParsingContext> iterator = csvParser.iterate(reader).iterator();
        TimeSeriesBuilder context = new TimeSeriesBuilder(VALUES);
        return read(iterator, context);
    }

    static Map<String, StringTimeSeries> read(ResultIterator<String[], ParsingContext> iterator, TimeSeriesBuilder context) {

        while (iterator.hasNext()) {
            String[] tokens = iterator.next();
            if (tokens.length != context.getExpectedTokens()) {
                throw new TimeSeriesException("Columns of line " + context.linesParsed() + " are inconsistent with header");
            }

            context.parseLine(tokens);
        }
        return context.createTimeSeries();
    }
}

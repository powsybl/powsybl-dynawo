/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeseries;

import com.powsybl.commons.PowsyblException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.commons.timeseries.TimeSeriesConstants.NB_COLUMNS;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class CsvTimeLineParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvTimeLineParser.class);

    private CsvTimeLineParser() {
    }

    public static List<Event> parseCsv(Path file) {
        return parseCsv(file, '|');
    }

    public static List<Event> parseCsv(Path file, char separator) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parseCsv(reader, separator);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static List<Event> parseCsv(BufferedReader reader, char separator) {
        Objects.requireNonNull(reader);
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(separator);
        settings.getFormat().setQuoteEscape('"');
        settings.getFormat().setLineSeparator(System.lineSeparator());
        settings.setMaxColumns(NB_COLUMNS);
        CsvParser csvParser = new CsvParser(settings);
        ResultIterator<String[], ParsingContext> iterator = csvParser.iterate(reader).iterator();
        return read(iterator);
    }

    static List<Event> read(ResultIterator<String[], ParsingContext> iterator) {
        List<Event> timeLineSeries = new ArrayList<>();
        int iLine = 0;
        while (iterator.hasNext()) {
            iLine++;
            String[] tokens = iterator.next();
            if (tokens.length != NB_COLUMNS) {
                throw new PowsyblException("Columns of line " + iLine + " are inconsistent");
            }
            String time = tokens[0];
            String modelName = tokens[1];
            String message = tokens[2];
            if (time == null || modelName == null || message == null) {
                LOGGER.warn("Inconsistent event entry (time: '{}', modelName: '{}', message: '{}')", time, modelName, message);
            } else {
                try {
                    double timeD = Double.parseDouble(time);
                    timeLineSeries.add(new Event(timeD, modelName, message));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Inconsistent time entry '{}'", time);
                }
            }
        }
        return timeLineSeries;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.commons.PowsyblException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class CsvTimeLineParser implements TimeLineParser {

    private static final int NB_COLUMNS = 3;
    private final char separator;

    public CsvTimeLineParser() {
        this('|');
    }

    public CsvTimeLineParser(char separator) {
        this.separator = separator;
    }

    public List<Event> parse(Path file) {
        return parse(file, separator);
    }

    public static List<Event> parse(Path file, char separator) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parse(reader, separator);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static List<Event> parse(BufferedReader reader, char separator) {
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
        List<Event> timeline = new ArrayList<>();
        int iLine = 0;
        while (iterator.hasNext()) {
            iLine++;
            String[] tokens = iterator.next();
            if (tokens.length != NB_COLUMNS) {
                throw new PowsyblException("Columns of line " + iLine + " are inconsistent");
            }
            TimeLineUtil.createEvent(tokens[0], tokens[1], tokens[2])
                    .ifPresent(timeline::add);
        }
        return timeline;
    }

}

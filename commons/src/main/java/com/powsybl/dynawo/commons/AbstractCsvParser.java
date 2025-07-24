/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

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
import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractCsvParser<T> {

    protected static final char DEFAULT_SEPARATOR = '|';

    protected CsvParser csvParser;

    protected static CsvParserSettings setupSettings(char separator, boolean skipHeader) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(separator);
        settings.getFormat().setQuoteEscape('"');
        settings.getFormat().setLineSeparator(System.lineSeparator());
        settings.setHeaderExtractionEnabled(skipHeader);
        return settings;
    }

    public List<T> parse(Path file) {
        if (!Files.exists(file)) {
            return Collections.emptyList();
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Objects.requireNonNull(reader);
            return read(csvParser.iterate(reader).iterator());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected List<T> read(ResultIterator<String[], ParsingContext> iterator) {
        List<T> logs = new ArrayList<>();
        int iLine = 0;
        while (iterator.hasNext()) {
            iLine++;
            String[] tokens = iterator.next();
            if (!hasCorrectNbColumns(tokens.length)) {
                throw new PowsyblException("Columns of line " + iLine + " are inconsistent");
            }
            createEntry(tokens).ifPresent(logs::add);
        }
        return logs;
    }

    protected abstract Optional<T> createEntry(String[] tokens);

    protected abstract boolean hasCorrectNbColumns(int tokensSize);
}

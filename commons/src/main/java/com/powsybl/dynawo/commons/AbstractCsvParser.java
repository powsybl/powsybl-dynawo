/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.FieldMismatchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractCsvParser<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCsvParser.class);

    protected static final char DEFAULT_SEPARATOR = '|';

    private final char separator;
    private final boolean skipHeader;
    private final int maxColumns;

    protected AbstractCsvParser(char separator, boolean skipHeader) {
        this(separator, skipHeader, -1);
    }

    protected AbstractCsvParser(char separator, boolean skipHeader, int maxColumns) {
        this.separator = separator;
        this.skipHeader = skipHeader;
        this.maxColumns = maxColumns;
    }

    public List<T> parse(Path file) {
        if (!Files.exists(file)) {
            return Collections.emptyList();
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             CsvReader<CsvRecord> csvReader = CsvReader.builder()
                     .fieldSeparator(separator)
                     .quoteCharacter('"')
                     .missingFieldStrategy(FieldMismatchStrategy.IGNORE)
                     .extraFieldStrategy(FieldMismatchStrategy.IGNORE)
                     .trimWhitespacesAroundQuotes(true)
                     .skipEmptyLines(true)
                     .ofCsvRecord(reader)) {
            if (skipHeader) {
                csvReader.skipLines(1);
            }
            List<T> results = new ArrayList<>();
            AtomicInteger lineIndex = new AtomicInteger(0);
            csvReader.forEach(csvRecord ->
                    parseRecord(csvRecord, lineIndex.incrementAndGet()).ifPresent(results::add));
            return results;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Optional<T> parseRecord(CsvRecord csvRecord, int iLine) {
        int size = maxColumns >= 0 && maxColumns < csvRecord.getFieldCount() ?
                maxColumns : csvRecord.getFieldCount();
        if (!hasCorrectNbColumns(size)) {
            LOGGER.warn("Columns of line {} are inconsistent, the line will be skipped", iLine);
            return Optional.empty();
        }
        List<String> fields = csvRecord.getFields();
        String[] tokens = new String[size];
        for (int i = 0; i < fields.size(); i++) {
            tokens[i] = fields.get(i).trim();
            if (tokens[i].isEmpty()) {
                tokens[i] = null;
            }
        }
        return createEntry(tokens);
    }

    protected abstract Optional<T> createEntry(String[] tokens);

    protected abstract boolean hasCorrectNbColumns(int tokensSize);
}

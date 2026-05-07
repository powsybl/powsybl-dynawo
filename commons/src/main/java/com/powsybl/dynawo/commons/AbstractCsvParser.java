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

    private char separator = DEFAULT_SEPARATOR;
    private boolean skipHeader = false;
    private int maxColumns = -1;

    protected AbstractCsvParser(char separator, boolean skipHeader) {
        this.separator = separator;
        this.skipHeader = skipHeader;
    }

    protected AbstractCsvParser(char separator, boolean skipHeader, int maxColumns) {
        this(separator, skipHeader);
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
                     .allowMissingFields(true)
                     .allowExtraFields(true)
                     .trimWhitespacesAroundQuotes(true)
                     .skipEmptyLines(true)
                     .ofCsvRecord(reader)) {
            if (skipHeader) {
                csvReader.skipLines(1);
            }
            List<T> logs = new ArrayList<>();
            AtomicInteger lineIndex = new AtomicInteger(0);
            csvReader.forEach(csvRecord -> {
                int iLine = lineIndex.incrementAndGet();
                int size = maxColumns >= 0 && maxColumns < csvRecord.getFieldCount() ?
                        maxColumns : csvRecord.getFieldCount();
                if (hasCorrectNbColumns(size)) {
                    List<String> fields = csvRecord.getFields();
                    String[] tokens = new String[size];
                    for (int i = 0; i < fields.size(); i++) {
                        tokens[i] = fields.get(i).trim();
                        if (tokens[i].isEmpty()) {
                            tokens[i] = null;
                        }
                    }
                    createEntry(tokens).ifPresent(logs::add);
                } else {
                    LOGGER.warn("Columns of line {} are inconsistent, the line will be skipped", iLine);
                }
            });
            return logs;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected abstract Optional<T> createEntry(String[] tokens);

    protected abstract boolean hasCorrectNbColumns(int tokensSize);
}

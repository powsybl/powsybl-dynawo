/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.dynawologs;

import com.powsybl.dynawo.commons.AbstractCsvParser;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CsvLogParser extends AbstractCsvParser<LogEntry> {

    private static final int NB_COLUMNS = 3;

    private static final String SPACED_SEPARATOR = " " + DEFAULT_SEPARATOR + " ";

    public CsvLogParser() {
        this(DEFAULT_SEPARATOR);
    }

    public CsvLogParser(char separator) {
        CsvParserSettings settings = setupSettings(separator, false);
        this.csvParser = new CsvParser(settings);
    }

    @Override
    protected Optional<LogEntry> createEntry(String[] tokens) {
        if (tokens.length > NB_COLUMNS) {
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < tokens.length - 1; i++) {
                builder.append(tokens[i]);
                builder.append(SPACED_SEPARATOR);
            }
            builder.append(tokens[tokens.length - 1]);
            return LogUtils.createLog(tokens[1], builder.toString());
        }
        return LogUtils.createLog(tokens[1], tokens[2]);
    }

    @Override
    protected boolean hasCorrectNbColumns(int tokensSize) {
        // extra column will be handled by createEntry
        return NB_COLUMNS <= tokensSize;
    }
}

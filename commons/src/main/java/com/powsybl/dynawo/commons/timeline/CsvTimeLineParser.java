/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.dynawo.commons.AbstractCsvParser;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CsvTimeLineParser extends AbstractCsvParser<TimelineEntry> implements TimeLineParser {

    private static final int MIN_NB_COLUMNS = 3;
    private static final int MAX_NB_COLUMNS = 4;

    public CsvTimeLineParser() {
        this(DEFAULT_SEPARATOR);
    }

    public CsvTimeLineParser(char separator) {
        CsvParserSettings settings = setupSettings(separator, false);
        settings.setMaxColumns(MAX_NB_COLUMNS);
        this.csvParser = new CsvParser(settings);
    }

    @Override
    protected Optional<TimelineEntry> createEntry(String[] tokens) {

        if (tokens.length == MIN_NB_COLUMNS) {
            return TimeLineUtil.createEvent(tokens[0], tokens[1], tokens[2], null);
        }
        return TimeLineUtil.createEvent(tokens[0], tokens[1], tokens[2], tokens[3]);
    }

    @Override
    protected boolean hasCorrectNbColumns(int tokensSize) {
        return MIN_NB_COLUMNS == tokensSize || MAX_NB_COLUMNS == tokensSize;
    }
}

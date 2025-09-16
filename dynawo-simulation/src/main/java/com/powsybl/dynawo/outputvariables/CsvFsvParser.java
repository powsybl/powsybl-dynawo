/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynawo.commons.AbstractCsvParser;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CsvFsvParser extends AbstractCsvParser<FsvEntry> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFsvParser.class);

    private static final int NB_COLUMNS = 4;

    public CsvFsvParser() {
        this(DEFAULT_SEPARATOR);
    }

    public CsvFsvParser(char separator) {
        CsvParserSettings settings = setupSettings(separator, true);
        settings.setMaxColumns(NB_COLUMNS);
        this.csvParser = new CsvParser(settings);
    }

    @Override
    protected Optional<FsvEntry> createEntry(String[] tokens) {
        String model = tokens[0];
        String variable = tokens[1];
        String value = tokens[2];
        if (model == null || variable == null || value == null) {
            LOGGER.warn("Inconsistent FSV entry (model: '{}', variable: '{}', value: '{}')", model, variable, value);
        } else {
            try {
                double valueD = Double.parseDouble(value);
                return Optional.of(new FsvEntry(model, variable, valueD));
            } catch (NumberFormatException e) {
                LOGGER.warn("Inconsistent value entry '{}'", value);
            }
        }
        return Optional.empty();
    }

    @Override
    protected boolean hasCorrectNbColumns(int tokensSize) {
        return tokensSize == NB_COLUMNS;
    }
}

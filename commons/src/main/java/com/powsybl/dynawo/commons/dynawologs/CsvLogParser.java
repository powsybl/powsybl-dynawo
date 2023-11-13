/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.dynawologs;

import com.powsybl.dynawo.commons.AbstractCsvParser;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CsvLogParser extends AbstractCsvParser<LogEntry> {

    private static final int NB_COLUMNS = 4;

    public CsvLogParser() {
        this(DEFAULT_SEPARATOR);
    }

    public CsvLogParser(char separator) {
        super(separator);
    }

    //TODO fix dynawo.log use of |
    @Override
    protected Optional<LogEntry> createEntry(String[] tokens) {
        return tokens.length == NB_COLUMNS ? LogUtils.createLog(tokens[1], tokens[2] + " " + tokens[3])
            : LogUtils.createLog(tokens[1], tokens[2]);
    }

    @Override
    protected boolean hasCorrectNbColumns(int tokensSize) {
        return tokensSize == NB_COLUMNS - 1 || tokensSize == NB_COLUMNS;
    }

    @Override
    protected int getNbColumns() {
        return NB_COLUMNS;
    }
}

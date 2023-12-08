/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.dynawo.commons.AbstractCsvParser;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CsvTimeLineParser extends AbstractCsvParser<TimelineEntry> implements TimeLineParser {

    private static final int NB_COLUMNS = 3;

    public CsvTimeLineParser() {
        this(DEFAULT_SEPARATOR);
    }

    public CsvTimeLineParser(char separator) {
        super(separator);
    }

    @Override
    protected Optional<TimelineEntry> createEntry(String[] tokens) {
        return TimeLineUtil.createEvent(tokens[0], tokens[1], tokens[2]);
    }

    @Override
    protected boolean hasCorrectNbColumns(int tokensSize) {
        return NB_COLUMNS == tokensSize;
    }

    @Override
    protected int getNbColumns() {
        return NB_COLUMNS;
    }
}

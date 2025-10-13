/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class CsvTimeLineParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"/timeline.log", "/timelineWithQuotes.log"})
    void testTimeline(String fileName) throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource(fileName)).toURI());
        List<TimelineEntry> timeline = new CsvTimeLineParser().parse(path);
        assertEquals(5, timeline.size());
        assertTimeLineEntry(timeline.get(0), "PMIN : activation", "GEN____8_SM", 0.);
        assertTimeLineEntry(timeline.get(1), "PMIN : activation", "GEN____3_SM", 0.0306911);
        assertTimeLineEntry(timeline.get(2), "PMIN : deactivation", "GEN____8_SM", 0.348405);
        assertTimeLineEntry(timeline.get(3), "PMIN : deactivation", "GEN____3_SM", 0.828675);
        assertTimeLineEntry(timeline.get(4), "PMIN : activation", "GEN____8_SM", 0.834701);
    }

    @Test
    void testInconsistentLine() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/wrongTimeline.log")).toURI());
        List<TimelineEntry> timeline = new CsvTimeLineParser('|').parse(path);
        assertEquals(2, timeline.size());
        assertTimeLineEntry(timeline.get(0), "PMIN : activation", "GEN____8_SM", 0.);
        assertTimeLineEntry(timeline.get(1), "PMIN : deactivation", "GEN____8_SM", 0.348405);
    }

    private static void assertTimeLineEntry(TimelineEntry entry, String message, String modelName, double time) {
        assertEquals(message, entry.message());
        assertEquals(modelName, entry.modelName());
        assertEquals(time, entry.time(), 1e-9);
    }
}

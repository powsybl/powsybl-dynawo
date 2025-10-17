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

        assertEquals("PMIN : activation", timeline.get(0).message());
        assertEquals("GEN____8_SM", timeline.get(0).modelName());
        assertEquals(0., timeline.get(0).time(), 1e-9);
        assertEquals("PMIN : activation", timeline.get(1).message());
        assertEquals("GEN____3_SM", timeline.get(1).modelName());
        assertEquals(0.0306911, timeline.get(1).time(), 1e-9);
        assertEquals("PMIN : deactivation", timeline.get(2).message());
        assertEquals("GEN____8_SM", timeline.get(2).modelName());
        assertEquals("PMIN : deactivation", timeline.get(3).message());
        assertEquals("GEN____3_SM", timeline.get(3).modelName());
        assertEquals("PMIN : activation", timeline.get(4).message());
        assertEquals("GEN____8_SM", timeline.get(4).modelName());
    }

    @Test
    void testWithPriority() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/timelineWithPriority.log")).toURI());
        List<TimelineEntry> timeline = new CsvTimeLineParser().parse(path);
        assertTimeLineEntry(timeline.get(0), "PMIN : activation", "GEN____8_SM", 0, "2");
        assertTimeLineEntry(timeline.get(1), "PMIN : activation", "GEN____3_SM", 0.0306911, "2");
    }

    @Test
    void testInconsistentLine() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/wrongTimeline.log")).toURI());
        List<TimelineEntry> timeline = new CsvTimeLineParser('|').parse(path);
        assertEquals(2, timeline.size());
        assertEquals(timeline.get(0).message(), "PMIN : activation");
        assertEquals(timeline.get(0).modelName(), "GEN____8_SM");
        assertEquals(timeline.get(0).time(), 0.);
        assertEquals(timeline.get(1).message(), "PMIN : deactivation");
        assertEquals(timeline.get(1).modelName(), "GEN____8_SM");
        assertEquals(timeline.get(1).time(), 0.348405);
    }

    private static void assertTimeLineEntry(TimelineEntry entry, String message, String modelName, double time, String priority) {
        assertEquals(message, entry.message());
        assertEquals(modelName, entry.modelName());
        assertEquals(time, entry.time(), 1e-9);
        assertEquals(priority, entry.priority());
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.commons.PowsyblException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class CsvTimeLineParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"/timeline.log", "/timelineWithQuotes.log"})
    void testTimeline(String fileName) throws URISyntaxException {

        Path path = Path.of(Objects.requireNonNull(getClass().getResource(fileName)).toURI());
        List<TimelineEntry> timeline = new CsvTimeLineParser().parse(path);

        assertEquals(5, timeline.size());
        assertEquals("PMIN : activation", timeline.get(0).getMessage());
        assertEquals("GEN____8_SM", timeline.get(0).getModelName());
        assertEquals(0., timeline.get(0).getTime(), 1e-9);
        assertEquals("PMIN : activation", timeline.get(1).getMessage());
        assertEquals("GEN____3_SM", timeline.get(1).getModelName());
        assertEquals(0.0306911, timeline.get(1).getTime(), 1e-9);
        assertEquals("PMIN : deactivation", timeline.get(2).getMessage());
        assertEquals("GEN____8_SM", timeline.get(2).getModelName());
        assertEquals("PMIN : deactivation", timeline.get(3).getMessage());
        assertEquals("GEN____3_SM", timeline.get(3).getModelName());
        assertEquals("PMIN : activation", timeline.get(4).getMessage());
        assertEquals("GEN____8_SM", timeline.get(4).getModelName());
    }

    @Test
    void testInconsistentFile() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/wrongTimeline.log")).toURI());
        Exception e = assertThrows(PowsyblException.class, () -> CsvTimeLineParser.parse(path, '|'));
        assertEquals("Columns of line 2 are inconsistent", e.getMessage());
    }
}
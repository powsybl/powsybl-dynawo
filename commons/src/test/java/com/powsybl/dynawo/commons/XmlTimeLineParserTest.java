/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.dynawo.commons.timeseries.Event;
import com.powsybl.dynawo.commons.timeseries.XmlTimeLineParser;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class XmlTimeLineParserTest {

    @Test
    void test() throws XMLStreamException {

        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/timeline.xml")));
        List<Event> timeline = XmlTimeLineParser.parseXml(xml);

        assertEquals(5, timeline.size());
        assertEquals("PMIN : activation", timeline.get(0).getMessage());
        assertEquals("GEN____8_SM", timeline.get(0).getModelName());
        assertEquals(0., timeline.get(0).getTime(), 1e-9);
        assertEquals("PMIN : activation", timeline.get(1).getMessage());
        assertEquals("GEN____3_SM", timeline.get(1).getModelName());
        assertEquals(0.030691068513160655, timeline.get(1).getTime(), 1e-9);
        assertEquals("PMIN : deactivation", timeline.get(2).getMessage());
        assertEquals("GEN____8_SM", timeline.get(2).getModelName());
        assertEquals("PMIN : deactivation", timeline.get(3).getMessage());
        assertEquals("GEN____3_SM", timeline.get(3).getModelName());
        assertEquals("PMIN : activation", timeline.get(4).getMessage());
        assertEquals("GEN____8_SM", timeline.get(4).getModelName());
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/timeline.xml")).toURI());
        List<Event> timeline = XmlTimeLineParser.parseXml(path);
        assertEquals(5, timeline.size());
    }

    @Test
    void testInconsistentFile() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/wrongTimeline.xml")));
        List<Event> timeline = XmlTimeLineParser.parseXml(xml);
        assertEquals(4, timeline.size());
    }
}

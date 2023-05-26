/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import com.powsybl.dynawo.commons.timeseries.XmlTimeLineParser;

import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;
import org.junit.jupiter.api.Test;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class XmlTimeLineParserTest {

    @Test
    void test() throws XMLStreamException {

        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/timeline.xml")));
        Map<String, StringTimeSeries> timeSeries = XmlTimeLineParser.parseXml(xml);

        assertEquals(2, timeSeries.size());

        StringTimeSeries ts1 = timeSeries.get("modelName");
        StringTimeSeries ts2 = timeSeries.get("message");

        assertEquals("modelName", ts1.getMetadata().getName());
        assertEquals(TimeSeriesDataType.STRING, ts1.getMetadata().getDataType());
        assertArrayEquals(new String[] {"GEN____8_SM", "GEN____3_SM", "GEN____8_SM", "GEN____3_SM", "GEN____8_SM"}, ts1.toArray());

        assertEquals("message", ts2.getMetadata().getName());
        assertEquals(TimeSeriesDataType.STRING, ts2.getMetadata().getDataType());
        assertArrayEquals(new String[] {"PMIN : activation", "PMIN : activation", "PMIN : deactivation", "PMIN : deactivation", "PMIN : activation"}, ts2.toArray());
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/timeline.xml")).toURI());
        Map<String, StringTimeSeries> timeSeries = XmlTimeLineParser.parseXml(path);
        assertEquals(2, timeSeries.size());
    }

    @Test
    void testInconsistentFile() {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/wrongTimeline.xml")));
        Exception e = assertThrows(TimeSeriesException.class, () -> XmlTimeLineParser.parseXml(xml));
        assertEquals("Columns of line 1 are inconsistent with header", e.getMessage());
    }
}

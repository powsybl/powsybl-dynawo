/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.timeseries;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class XmlTimeLineParserTest {

    @Test
    public void test() throws XMLStreamException {
        String xml = String.join(System.lineSeparator(),
                "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>",
                "<timeline xmlns=\"http://www.rte-france.com/dynawo\">",
                "  <event time=\"0\" modelName=\"GEN____8_SM\" message=\"PMIN : activation\"/>",
                "  <event time=\"0.030691068513160655\" modelName=\"GEN____3_SM\" message=\"PMIN : activation\"/>",
                "  <event time=\"0.348405399018546\" modelName=\"GEN____8_SM\" message=\"PMIN : deactivation\"/>",
                "  <event time=\"0.82867509138207629\" modelName=\"GEN____3_SM\" message=\"PMIN : deactivation\"/>",
                "  <event time=\"0.83470144243143729\" modelName=\"GEN____8_SM\" message=\"PMIN : activation\"/>",
                "</timeline>") + System.lineSeparator();

        try (StringReader reader = new StringReader(xml)) {
            Map<String, TimeSeries> timeSeries = XmlTimeLineParser.parseXml(reader);

            assertEquals(2, timeSeries.size());

            TimeSeries ts1 = timeSeries.get("modelName");
            TimeSeries ts2 = timeSeries.get("message");

            assertEquals("modelName", ts1.getMetadata().getName());
            assertEquals(TimeSeriesDataType.STRING, ts1.getMetadata().getDataType());
            assertArrayEquals(new String[] {"GEN____8_SM", "GEN____3_SM", "GEN____8_SM", "GEN____3_SM", "GEN____8_SM"}, ((StringTimeSeries) ts1).toArray());

            assertEquals("message", ts2.getMetadata().getName());
            assertEquals(TimeSeriesDataType.STRING, ts2.getMetadata().getDataType());
            assertArrayEquals(new String[] {"PMIN : activation", "PMIN : activation", "PMIN : deactivation", "PMIN : deactivation", "PMIN : activation"}, ((StringTimeSeries) ts2).toArray());
        }

        String inconsistent = String.join(System.lineSeparator(),
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>",
            "<timeline xmlns=\"http://www.rte-france.com/dynawo\">",
            "  <event time=\"0\" modelName=\"GEN____8_SM\" message=\"PMIN : activation\"/>",
            "  <event time=\"0.030691068513160655\" modelName=\"GEN____3_SM\"/>",
            "  <event time=\"0.348405399018546\" modelName=\"GEN____8_SM\" message=\"PMIN : deactivation\"/>",
            "  <event time=\"0.82867509138207629\" modelName=\"GEN____3_SM\" message=\"PMIN : deactivation\"/>",
            "  <event time=\"0.83470144243143729\" modelName=\"GEN____8_SM\" message=\"PMIN : activation\"/>",
            "</timeline>") + System.lineSeparator();
        try (StringReader reader = new StringReader(inconsistent)) {
            assertThatCode(() -> XmlTimeLineParser.parseXml(reader)).hasMessageContaining("Columns of line 1 are inconsistent with header").isInstanceOf(TimeSeriesException.class);
        }
    }
}

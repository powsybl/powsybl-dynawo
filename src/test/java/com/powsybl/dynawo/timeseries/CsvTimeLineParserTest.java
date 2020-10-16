/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.timeseries;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CsvTimeLineParserTest {

    @Test
    public void test() {
        String csv = String.join(System.lineSeparator(),
                "0 | GEN____8_SM | PMIN : activation",
                "0.0306911 | GEN____3_SM | PMIN : activation",
                "0.348405 | GEN____8_SM | PMIN : deactivation",
                "0.828675 | GEN____3_SM | PMIN : deactivation",
                "0.834701 | GEN____8_SM | PMIN : activation") + System.lineSeparator();

        String csvWithQuotes = String.join(System.lineSeparator(),
                "\"0\" | \"GEN____8_SM\" | \"PMIN : activation\"",
                "\"0.0306911\" | \"GEN____3_SM\" | \"PMIN : activation\"",
                "\"0.348405\" | \"GEN____8_SM\" | \"PMIN : deactivation\"",
                "\"0.828675\" | \"GEN____3_SM\" | \"PMIN : deactivation\"",
                "\"0.834701\" | \"GEN____8_SM\" | \"PMIN : activation\"") + System.lineSeparator();

        Arrays.asList(csv, csvWithQuotes).forEach(data -> {
            Map<String, TimeSeries> timeSeries = CsvTimeLineParser.parseCsv(data, '|');

            assertEquals(2, timeSeries.size());

            TimeSeries ts1 = timeSeries.get("modelName");
            TimeSeries ts2 = timeSeries.get("message");

            assertEquals("modelName", ts1.getMetadata().getName());
            assertEquals(TimeSeriesDataType.STRING, ts1.getMetadata().getDataType());
            assertArrayEquals(new String[] {"GEN____8_SM", "GEN____3_SM", "GEN____8_SM", "GEN____3_SM", "GEN____8_SM"}, ((StringTimeSeries) ts1).toArray());

            assertEquals("message", ts2.getMetadata().getName());
            assertEquals(TimeSeriesDataType.STRING, ts2.getMetadata().getDataType());
            assertArrayEquals(new String[] {"PMIN : activation", "PMIN : activation", "PMIN : deactivation", "PMIN : deactivation", "PMIN : activation"}, ((StringTimeSeries) ts2).toArray());
        });

        String inconsistent = String.join(System.lineSeparator(),
            "0 | GEN____8_SM | PMIN : activation",
            "0.0306911 | PMIN : activation",
            "0.348405 | GEN____8_SM | PMIN : deactivation",
            "0.828675 | GEN____3_SM | PMIN : deactivation",
            "0.834701 | GEN____8_SM | PMIN : activation") + System.lineSeparator();
        assertThatCode(() -> CsvTimeLineParser.parseCsv(inconsistent, '|')).hasMessageContaining("Columns of line 1 are inconsistent with header").isInstanceOf(TimeSeriesException.class);
    }
}

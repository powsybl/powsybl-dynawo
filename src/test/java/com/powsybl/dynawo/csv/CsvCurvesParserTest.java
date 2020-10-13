/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.csv;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CsvCurvesParserTest {

    @Test
    public void test() {
        String csv = String.join(System.lineSeparator(),
                "time;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____2_TN_Upu_value;NETWORK__BUS____3_TN_Upu_value;NETWORK__BUS____4_TN_Upu_value",
                "0.000000;1.059970;1.045041;1.010101;1.017703",
                "0.000001;1.059970;1.045041;1.010201;1.017703",
                "0.000002;1.059970;1.045041;1.010001;1.017703",
                "0.000004;1.059970;1.045041;1.010101;1.017703") + System.lineSeparator();

        String csvWithQuotes = String.join(System.lineSeparator(),
                "\"time\";\"NETWORK__BUS____1_TN_Upu_value\";\"NETWORK__BUS____2_TN_Upu_value\";\"NETWORK__BUS____3_TN_Upu_value\";\"NETWORK__BUS____4_TN_Upu_value\"",
                "\"0.000000\";\"1.059970\";\"1.045041\";\"1.010101\";\"1.017703\"",
                "\"0.000001\";\"1.059970\";\"1.045041\";\"1.010201\";\"1.017703\"",
                "\"0.000002\";\"1.059970\";\"1.045041\";\"1.010001\";\"1.017703\"",
                "\"0.000004\";\"1.059970\";\"1.045041\";\"1.010101\";\"1.017703\"") + System.lineSeparator();

        Arrays.asList(csv, csvWithQuotes).forEach(data -> {
            Map<String, TimeSeries> timeSeries = CsvCurvesParser.parseCsv(data, ';');

            assertEquals(4, timeSeries.size());

            TimeSeries ts1 = timeSeries.get("NETWORK__BUS____1_TN_Upu_value");
            TimeSeries ts2 = timeSeries.get("NETWORK__BUS____2_TN_Upu_value");
            TimeSeries ts3 = timeSeries.get("NETWORK__BUS____3_TN_Upu_value");
            TimeSeries ts4 = timeSeries.get("NETWORK__BUS____4_TN_Upu_value");

            assertEquals("NETWORK__BUS____1_TN_Upu_value", ts1.getMetadata().getName());
            assertEquals(TimeSeriesDataType.DOUBLE, ts1.getMetadata().getDataType());
            assertArrayEquals(new double[] {1.059970, 1.059970, 1.059970, 1.059970}, ((DoubleTimeSeries) ts1).toArray(), 0);

            assertEquals("NETWORK__BUS____2_TN_Upu_value", ts2.getMetadata().getName());
            assertEquals(TimeSeriesDataType.DOUBLE, ts2.getMetadata().getDataType());
            assertArrayEquals(new double[] {1.045041, 1.045041, 1.045041, 1.045041}, ((DoubleTimeSeries) ts2).toArray(), 0);

            assertEquals("NETWORK__BUS____3_TN_Upu_value", ts3.getMetadata().getName());
            assertEquals(TimeSeriesDataType.DOUBLE, ts3.getMetadata().getDataType());
            assertArrayEquals(new double[] {1.010101, 1.010201, 1.010001, 1.010101}, ((DoubleTimeSeries) ts3).toArray(), 0);

            assertEquals("NETWORK__BUS____4_TN_Upu_value", ts4.getMetadata().getName());
            assertEquals(TimeSeriesDataType.DOUBLE, ts4.getMetadata().getDataType());
            assertArrayEquals(new double[] {1.017703, 1.017703, 1.017703, 1.017703}, ((DoubleTimeSeries) ts4).toArray(), 0);

        });

        String emptyCsv = "";
        assertThatCode(() -> CsvCurvesParser.parseCsv(emptyCsv, ';')).hasMessage("CSV header is missing").isInstanceOf(TimeSeriesException.class);

        String badHeader = String.join(System.lineSeparator(),
                "Timed;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____2_TN_Upu_value;NETWORK__BUS____3_TN_Upu_value;NETWORK__BUS____4_TN_Upu_value",
                "0.000000;1.059970;1.045041;1.010101;1.017703",
                "0.000001;1.059970;1.045041;1.010201;1.017703",
                "0.000002;1.059970;1.045041;1.010001;1.017703",
                "0.000004;1.059970;1.045041;1.010101;1.017703") + System.lineSeparator();
        assertThatCode(() -> CsvCurvesParser.parseCsv(badHeader, ';')).hasMessage("Bad CSV header, should be \ntime;...").isInstanceOf(TimeSeriesException.class);

        String duplicates = String.join(System.lineSeparator(),
                "time;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____2_TN_Upu_value;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____4_TN_Upu_value",
                "0.000000;1.059970;1.045041;1.010101;1.017703",
                "0.000001;1.059970;1.045041;1.010201;1.017703",
                "0.000002;1.059970;1.045041;1.010001;1.017703",
                "0.000004;1.059970;1.045041;1.010101;1.017703") + System.lineSeparator();
        assertThatCode(() -> CsvCurvesParser.parseCsv(duplicates, ';')).hasMessageContaining("Bad CSV header, there are duplicates in time series names").isInstanceOf(TimeSeriesException.class);

        String inconsistent = String.join(System.lineSeparator(),
            "time;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____2_TN_Upu_value;NETWORK__BUS____3_TN_Upu_value;NETWORK__BUS____4_TN_Upu_value",
            "0.000000;1.059970;1.045041;1.010101;1.017703",
            "0.000001;1.045041;1.010201;1.017703",
            "0.000002;1.059970;1.045041;1.010001;1.017703",
            "0.000004;1.059970;1.045041;1.010101;1.017703") + System.lineSeparator();
        assertThatCode(() -> CsvCurvesParser.parseCsv(inconsistent, ';')).hasMessageContaining("Columns of line 1 are inconsistent with header").isInstanceOf(TimeSeriesException.class);

        String wrongType = String.join(System.lineSeparator(),
            "time;NETWORK__BUS____1_TN_Upu_value;NETWORK__BUS____2_TN_Upu_value;NETWORK__BUS____3_TN_Upu_value;NETWORK__BUS____4_TN_Upu_value",
            "0.000000;1.059970;value;1.010101;1.017703",
            "0.000001;1.059970;1.045041;1.010201;1.017703",
            "0.000002;1.059970;1.045041;1.010001;1.017703",
            "0.000004;1.059970;1.045041;1.010101;1.017703") + System.lineSeparator();
        assertThatCode(() -> CsvCurvesParser.parseCsv(wrongType, ';')).hasMessageContaining("Unexpected data type DOUBLE").isInstanceOf(TimeSeriesException.class);
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesDataType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoResultTest {

    private FileSystem fileSystem;

    @Before
    public void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Files.copy(getClass().getResourceAsStream("/curves.csv"), fileSystem.getPath("/curves.csv"));
    }

    @Test
    public void test() {
        DynawoResult result = new DynawoResult(fileSystem.getPath("/curves.csv"));

        assertTrue(result.isOk());

        assertEquals(4, result.getTimeSeries().size());

        TimeSeries ts1 = result.getTimeSeries().get(0);
        TimeSeries ts2 = result.getTimeSeries().get(1);
        TimeSeries ts3 = result.getTimeSeries().get(2);
        TimeSeries ts4 = result.getTimeSeries().get(3);

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
    }
}

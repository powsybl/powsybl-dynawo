package com.powsybl.dynawo.timeseries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.powsybl.timeseries.BigDoubleBuffer;
import com.powsybl.timeseries.BigStringBuffer;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesDataType;
import com.powsybl.timeseries.TimeSeriesIndex;
import com.powsybl.timeseries.TimeSeriesMetadata;

public final class CsvTimeSeriesSerializer {

    private static final int CACHE_SIZE = 10;

    private static IntFunction<ByteBuffer> byteBufferAllocator;

    private static BigDoubleBuffer doubleBuffer;

    private static BigStringBuffer stringBuffer;

    private CsvTimeSeriesSerializer() {
        byteBufferAllocator = ByteBuffer::allocateDirect;
        doubleBuffer = null;
        stringBuffer = null;
    }

    public static void writeZippedCsv(ZipOutputStream zos, String entryName, Map<String, TimeSeries> timeSeries) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zos.putNextEntry(zipEntry);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos));
        writeCsv(writer, TimeSeriesConstants.DEFAULT_SEPARATOR, timeSeries);
    }

    private static void writeCsv(Writer writer, char separator, Map<String, TimeSeries> timeSeries) {
        Objects.requireNonNull(writer);

        Stopwatch stopWatch = Stopwatch.createStarted();

        try {
            List<TimeSeries> listTimeSeries = timeSeries.values().stream().sorted(Comparator.comparing(ts -> ts.getMetadata().getName())).collect(Collectors.toList());
            TimeSeriesIndex tableIndex = getTimeSeriesIndex(listTimeSeries);

            writeHeader(writer, separator, listTimeSeries);
            fillBuffer(tableIndex, listTimeSeries);

            for (int point = 0; point < tableIndex.getPointCount(); point += CACHE_SIZE) {
                writePoint(writer, separator, tableIndex, listTimeSeries, point);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        LoggerFactory.getLogger(CsvTimeSeriesSerializer.class).info("Csv written in {} ms", stopWatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private static TimeSeriesIndex getTimeSeriesIndex(List<TimeSeries> listTimeSeries) {
        return listTimeSeries.get(0).getMetadata().getIndex();
    }

    private static void writeHeader(Writer writer, char separator, List<TimeSeries> listTimeSeries) throws IOException {
        // write header
        writer.write("Time");
        for (TimeSeries timeSeries : listTimeSeries) {
            TimeSeriesMetadata metadata = timeSeries.getMetadata();
            writer.write(separator);
            writer.write(metadata.getName());
        }
        writer.write(System.lineSeparator());
    }

    private static void fillBuffer(TimeSeriesIndex tableIndex, List<TimeSeries> listTimeSeries) {
        long timeSeriesOffset = getTimeSeriesOffset(tableIndex, listTimeSeries.size());
        long bufferSize = listTimeSeries.size() * tableIndex.getPointCount();
        TimeSeriesMetadata metadata = listTimeSeries.get(0).getMetadata();
        if (metadata.getDataType().equals(TimeSeriesDataType.DOUBLE)) {
            doubleBuffer = new BigDoubleBuffer(byteBufferAllocator, bufferSize);
            for (TimeSeries timeSeries : listTimeSeries) {
                timeSeries.synchronize(tableIndex);
                ((DoubleTimeSeries) timeSeries).fillBuffer(doubleBuffer, timeSeriesOffset);
            }
        } else if (metadata.getDataType() == TimeSeriesDataType.STRING) {
            stringBuffer = new BigStringBuffer(byteBufferAllocator, bufferSize);
            for (TimeSeries timeSeries : listTimeSeries) {
                timeSeries.synchronize(tableIndex);
                ((StringTimeSeries) timeSeries).fillBuffer(stringBuffer, timeSeriesOffset);
            }
        } else {
            throw new AssertionError("Unexpected data type " + metadata.getDataType());
        }
    }

    private static long getTimeSeriesOffset(TimeSeriesIndex tableIndex, int timeSeriesNum) {
        return (long) timeSeriesNum * tableIndex.getPointCount();
    }

    private static void writePoint(Writer writer, char separator, TimeSeriesIndex tableIndex, List<TimeSeries> listTimeSeries, int point) throws IOException {
        long timeSeriesOffset = getTimeSeriesOffset(tableIndex, listTimeSeries.size());
        long time = tableIndex.getTimeAt(point);
        writer.write(Long.toString(time));
        for (TimeSeries timeSeries : listTimeSeries) {
            writer.write(separator);
            if (timeSeries.getMetadata().getDataType() == TimeSeriesDataType.DOUBLE) {
                double value = doubleBuffer.get(timeSeriesOffset + point);
                writer.write(Double.toString(value));
            } else if (timeSeries.getMetadata().getDataType() == TimeSeriesDataType.STRING) {
                writer.write(stringBuffer.getString(timeSeriesOffset + point));
            } else {
                throw new AssertionError("Unexpected data type " + timeSeries.getMetadata().getDataType());
            }
        }
        writer.write(System.lineSeparator());
    }

}

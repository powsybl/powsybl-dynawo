/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.timeseries;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class XmlTimeLineParser {

    private XmlTimeLineParser() {
    }

    public static Map<String, TimeSeries> parseXml(Path timeLineFile) {
        Objects.requireNonNull(timeLineFile);

        try (Reader reader = Files.newBufferedReader(timeLineFile, StandardCharsets.UTF_8)) {
            return parseXml(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static Map<String, TimeSeries> parseXml(Reader reader) throws XMLStreamException {

        Map<String, TimeSeries> timeLineSeries = new HashMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        XMLStreamReader xmlReader = null;
        try {
            xmlReader = factory.createXMLStreamReader(reader);
            read(xmlReader, timeLineSeries);
        } finally {
            xmlReader.close();
        }
        return timeLineSeries;
    }

    private static void read(XMLStreamReader xmlReader, Map<String, TimeSeries> timeLineSeries) throws XMLStreamException {
        List<String> names = Arrays.asList("modelName", "message");
        ParsingContext context = new ParsingContext(names);
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readUntilEndElement("timeline", xmlReader, () -> {
            if (xmlReader.getLocalName().equals("event")) {
                String time = xmlReader.getAttributeValue(null, "time");
                String modelName = xmlReader.getAttributeValue(null, "modelName");
                String message = xmlReader.getAttributeValue(null, "message");
                if (time == null || modelName == null || message == null) {
                    throw new TimeSeriesException("Columns of line " + context.times.size() + " are inconsistent with header");
                }
                context.parseLine(Arrays.asList(time, modelName, message));
            }
        });
        timeLineSeries.putAll(context.createTimeSeries());
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeseries;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.timeseries.StringTimeSeries;
import com.powsybl.timeseries.TimeSeriesException;

import static com.powsybl.dynawo.commons.timeseries.TimeSeriesConstants.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class XmlTimeLineParser {

    private XmlTimeLineParser() {
    }

    public static Map<String, StringTimeSeries> parseXml(Path timeLineFile) {
        Objects.requireNonNull(timeLineFile);

        try (Reader reader = Files.newBufferedReader(timeLineFile, StandardCharsets.UTF_8)) {
            return parseXml(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static Map<String, StringTimeSeries> parseXml(Reader reader) throws XMLStreamException {

        Map<String, StringTimeSeries> timeLineSeries;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        XMLStreamReader xmlReader = null;
        try {
            xmlReader = factory.createXMLStreamReader(reader);
            timeLineSeries = read(xmlReader);
        } finally {
            if (xmlReader !=  null) {
                xmlReader.close();
            }
        }
        return timeLineSeries;
    }

    private static Map<String, StringTimeSeries> read(XMLStreamReader xmlReader) throws XMLStreamException {
        TimeSeriesBuilder context = new TimeSeriesBuilder(VALUES);
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readUntilEndElement("timeline", xmlReader, () -> {
            if (xmlReader.getLocalName().equals("event")) {
                String time = xmlReader.getAttributeValue(null, TIME);
                String modelName = xmlReader.getAttributeValue(null, MODEL_NAME);
                String message = xmlReader.getAttributeValue(null, MESSAGE);
                if (time == null || modelName == null || message == null) {
                    throw new TimeSeriesException("Columns of line " + context.linesParsed() + " are inconsistent with header");
                }
                context.parseLine(time, modelName, message);
            }
        });
        return context.createTimeSeries();
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class XmlTimeLineParser implements TimeLineParser {

    private static final String TIME = "time";
    private static final String MODEL_NAME = "modelName";
    private static final String MESSAGE = "message";

    public List<TimelineEntry> parse(Path timeLineFile) {
        Objects.requireNonNull(timeLineFile);
        if (!Files.exists(timeLineFile)) {
            return Collections.emptyList();
        }

        try (Reader reader = Files.newBufferedReader(timeLineFile, StandardCharsets.UTF_8)) {
            return parse(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static List<TimelineEntry> parse(Reader reader) throws XMLStreamException {

        List<TimelineEntry> timeLineSeries;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        XMLStreamReader xmlReader = null;
        try {
            xmlReader = factory.createXMLStreamReader(reader);
            timeLineSeries = read(xmlReader);
        } finally {
            if (xmlReader != null) {
                xmlReader.close();
            }
        }
        return timeLineSeries;
    }

    private static List<TimelineEntry> read(XMLStreamReader xmlReader) throws XMLStreamException {
        List<TimelineEntry> timeline = new ArrayList<>();
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readUntilEndElement("timeline", xmlReader, () -> {
            if (xmlReader.getLocalName().equals("event")) {
                String time = xmlReader.getAttributeValue(null, TIME);
                String modelName = xmlReader.getAttributeValue(null, MODEL_NAME);
                String message = xmlReader.getAttributeValue(null, MESSAGE);
                TimeLineUtil.createEvent(time, modelName, message)
                        .ifPresent(timeline::add);
            }
        });
        return timeline;
    }
}

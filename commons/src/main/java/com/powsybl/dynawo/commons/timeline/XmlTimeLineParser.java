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
import com.powsybl.dynawo.commons.AbstractXmlParser;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class XmlTimeLineParser extends AbstractXmlParser<TimelineEntry> implements TimeLineParser {

    private static final String TIME = "time";
    private static final String MODEL_NAME = "modelName";
    private static final String MESSAGE = "message";
    private static final String PRIORITY = "priority";

    @Override
    protected void read(XMLStreamReader xmlReader, Consumer<TimelineEntry> consumer) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readSubElements(xmlReader, elementName -> {
            try {
                if (elementName.equals("event")) {
                    String time = xmlReader.getAttributeValue(null, TIME);
                    String modelName = xmlReader.getAttributeValue(null, MODEL_NAME);
                    String message = xmlReader.getAttributeValue(null, MESSAGE);
                    String priority = xmlReader.getAttributeValue(null, PRIORITY);
                    XmlUtil.readEndElementOrThrow(xmlReader);
                    TimeLineUtil.createEvent(time, modelName, message,priority)
                            .ifPresent(consumer);
                }
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        });
    }
}

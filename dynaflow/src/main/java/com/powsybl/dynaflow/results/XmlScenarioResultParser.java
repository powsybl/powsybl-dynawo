/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.commons.AbstractXmlParser;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class XmlScenarioResultParser extends AbstractXmlParser<ScenarioResult> {

    private static final String STATUS = "status";
    private static final String ID = "id";
    private static final String TIME = "time";

    @Override
    protected void read(XMLStreamReader xmlReader, Consumer<ScenarioResult> consumer) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readSubElements(xmlReader, elementName -> readScenarioResult(elementName, xmlReader, consumer));
    }

    public static void readScenarioResult(String elementName, XMLStreamReader xmlReader, Consumer<ScenarioResult> scenarioConsumer) {
        if (elementName.equals("scenarioResults")) {
            String id = xmlReader.getAttributeValue(null, ID);
            String status = xmlReader.getAttributeValue(null, STATUS);
            List<FailedCriterion> failedCriteria = new ArrayList<>();
            XmlUtil.readSubElements(xmlReader, subElementName -> readFailedCriterion(subElementName, xmlReader, failedCriteria::add));
            ResultsUtil.createScenarioResult(id, status, failedCriteria).ifPresent(scenarioConsumer);
        }
    }

    public static void readFailedCriterion(String elementName, XMLStreamReader xmlReader, Consumer<FailedCriterion> resultConsumer) {
        try {
            if (elementName.equals("criterionNonRespected")) {
                String message = xmlReader.getAttributeValue(null, ID);
                String time = xmlReader.getAttributeValue(null, TIME);
                XmlUtil.readEndElementOrThrow(xmlReader);
                ResultsUtil.createFailedCriterion(message, time).ifPresent(resultConsumer);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}

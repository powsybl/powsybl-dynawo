/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.commons.AbstractXmlParser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractXmlAggregatedResultParser<T> extends AbstractXmlParser<T> {

    protected static final String STATUS = "status";
    private static final String ID = "id";
    private static final String TIME = "time";

    protected static boolean readScenarioResult(String elementName, XMLStreamReader xmlReader, Consumer<ScenarioResult> scenarioConsumer) {
        if (elementName.equals("scenarioResults")) {
            String id = xmlReader.getAttributeValue(null, ID);
            String status = xmlReader.getAttributeValue(null, STATUS);
            List<FailedCriterion> failedCriteria = new ArrayList<>();
            XmlUtil.readSubElements(xmlReader, subElementName -> readFailedCriterion(subElementName, xmlReader, failedCriteria::add));
            ResultsUtil.createScenarioResult(id, status, failedCriteria).ifPresent(scenarioConsumer);
            return true;
        }
        return false;
    }

    protected static void readFailedCriterion(String elementName, XMLStreamReader xmlReader, Consumer<FailedCriterion> resultConsumer) {
        try {
            if (elementName.equals("criterionNonRespected")) {
                String description = xmlReader.getAttributeValue(null, ID);
                String time = xmlReader.getAttributeValue(null, TIME);
                XmlUtil.readEndElementOrThrow(xmlReader);
                ResultsUtil.createFailedCriterion(description, time).ifPresent(resultConsumer);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}

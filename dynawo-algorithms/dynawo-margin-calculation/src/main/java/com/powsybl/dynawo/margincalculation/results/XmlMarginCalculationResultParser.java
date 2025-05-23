/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.results;

import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.contingency.xml.AbstractXmlAggregatedResultParser;
import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class XmlMarginCalculationResultParser extends AbstractXmlAggregatedResultParser<LoadIncreaseResult> {

    private static final String LOAD_LEVEL = "loadLevel";

    @Override
    protected void read(XMLStreamReader xmlReader, Consumer<LoadIncreaseResult> consumer) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readSubElements(xmlReader, elementName -> readLoadIncreaseResult(elementName, xmlReader, consumer));
    }

    private static void readLoadIncreaseResult(String elementName, XMLStreamReader xmlReader, Consumer<LoadIncreaseResult> resultConsumer) {
        if (elementName.equals("loadIncreaseResults")) {
            String loadLevel = xmlReader.getAttributeValue(null, LOAD_LEVEL);
            String status = xmlReader.getAttributeValue(null, STATUS);
            List<ScenarioResult> scenarioResults = new ArrayList<>();
            List<FailedCriterion> failedCriteria = new ArrayList<>();
            XmlUtil.readSubElements(xmlReader, subElementName -> readLoadIncreaseResultSubElements(subElementName, xmlReader, scenarioResults::add, failedCriteria::add));
            LoadIncreaseResultsUtil.createLoadIncreaseResult(loadLevel, status, scenarioResults, failedCriteria).ifPresent(resultConsumer);
        }
    }

    private static void readLoadIncreaseResultSubElements(String elementName, XMLStreamReader xmlReader, Consumer<ScenarioResult> scenarioConsumer, Consumer<FailedCriterion> criterionConsumer) {
        if (!readScenarioResult(elementName, xmlReader, scenarioConsumer)) {
            readFailedCriterion(elementName, xmlReader, criterionConsumer);
        }
    }
}

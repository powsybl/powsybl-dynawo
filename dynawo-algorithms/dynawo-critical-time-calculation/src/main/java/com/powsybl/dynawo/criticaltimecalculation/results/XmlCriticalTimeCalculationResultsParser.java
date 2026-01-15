/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.results;

import com.powsybl.dynawo.contingency.xml.AbstractXmlAggregatedResultParser;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.function.Consumer;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class XmlCriticalTimeCalculationResultsParser extends AbstractXmlAggregatedResultParser<CriticalTimeCalculationResult> {

    private static final String ID = "id";
    private static final String CRITICAL_TIME = "criticalTime";

    @Override
    protected void read(XMLStreamReader xmlReader, Consumer<CriticalTimeCalculationResult> consumer) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        while (xmlReader.hasNext()) {
            state = xmlReader.next();

            if (state == XMLStreamConstants.START_ELEMENT) {
                readCriticalTimeCalculationResult(xmlReader.getLocalName(), xmlReader, consumer);
            }
        }
    }

    private static void readCriticalTimeCalculationResult(String elementName, XMLStreamReader xmlReader, Consumer<CriticalTimeCalculationResult> resultConsumer) {
        if (elementName.equals("scenarioResults")) {
            String id = xmlReader.getAttributeValue(null, ID);
            String status = xmlReader.getAttributeValue(null, STATUS);
            String criticalTime = xmlReader.getAttributeValue(null, CRITICAL_TIME);
            CriticalTimeCalculationResultUtil.createCriticalTimeCalculationResult(id, status, criticalTime).ifPresent(resultConsumer);
        }
    }
}

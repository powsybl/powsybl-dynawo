/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.contingency.xml;

import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.contingency.results.ScenarioResult;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class XmlScenarioResultParser extends AbstractXmlAggregatedResultParser<ScenarioResult> {

    @Override
    protected void read(XMLStreamReader xmlReader, Consumer<ScenarioResult> consumer) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readSubElements(xmlReader, elementName -> readScenarioResult(elementName, xmlReader, consumer));
    }
}

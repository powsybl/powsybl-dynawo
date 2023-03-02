/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MacroConnect {

    private final String id;
    private final List<Pair<String, String>> attributesFrom;
    private final List<Pair<String, String>> attributesTo;

    public MacroConnect(String id, List<Pair<String, String>> attributesFrom, List<Pair<String, String>> attributesTo) {
        this.id = id;
        this.attributesFrom = attributesFrom;
        this.attributesTo = attributesTo;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", id);
        for (Pair<String, String> attribute : attributesFrom) {
            writer.writeAttribute(attribute.getKey(), attribute.getValue());
        }
        for (Pair<String, String> attribute : attributesTo) {
            writer.writeAttribute(attribute.getKey(), attribute.getValue());
        }
    }
}

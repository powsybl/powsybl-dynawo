/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MacroConnect {

    private final String id;
    private final List<MacroConnectAttribute> attributesFrom;
    private final List<MacroConnectAttribute> attributesTo;

    public MacroConnect(String id, List<MacroConnectAttribute> attributesFrom, List<MacroConnectAttribute> attributesTo) {
        this.id = id;
        this.attributesFrom = attributesFrom;
        this.attributesTo = attributesTo;
    }

    public MacroConnect(String id, List<MacroConnectAttribute> attributesFrom) {
        this.id = id;
        this.attributesFrom = attributesFrom;
        this.attributesTo = getDefaultAttributesTo();
    }

    public static List<MacroConnectAttribute> getDefaultAttributesTo() {
        return List.of(MacroConnectAttribute.of("id2", "NETWORK"));
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", id);
        for (MacroConnectAttribute attribute : attributesFrom) {
            writer.writeAttribute(attribute.getName(), attribute.getValue());
        }
        for (MacroConnectAttribute attribute : attributesTo) {
            writer.writeAttribute(attribute.getName(), attribute.getValue());
        }
    }
}

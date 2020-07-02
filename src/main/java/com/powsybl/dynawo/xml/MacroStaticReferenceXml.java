/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class MacroStaticReferenceXml {

    private MacroStaticReferenceXml() {
    }

    public static void writeStaticRef(XMLStreamWriter writer, String var, String staticVar) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", var);
        writer.writeAttribute("staticVar", staticVar);
    }

    public static void writeMacroStaticRef(XMLStreamWriter writer, String id) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroStaticRef");
        writer.writeAttribute("id", id);
    }

}

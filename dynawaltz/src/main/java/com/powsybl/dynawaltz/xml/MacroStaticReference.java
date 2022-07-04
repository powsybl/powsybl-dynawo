/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_STATIC_REFERENCE_PREFIX;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class MacroStaticReference {

    private final String lib;
    private final List<Pair<String, String>> varMapping;

    public MacroStaticReference(String lib, List<Pair<String, String>> varMapping) {
        this.lib = Objects.requireNonNull(lib);
        this.varMapping = Objects.requireNonNull(varMapping);
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        if (varMapping.isEmpty()) {
            return;
        }
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        writer.writeAttribute("id", MACRO_STATIC_REFERENCE_PREFIX + lib);
        for (Pair<String, String> varStaticVarPair : varMapping) {
            writeStaticRef(writer, varStaticVarPair.getLeft(), varStaticVarPair.getRight());
        }
        writer.writeEndElement();
    }

    public static void writeStaticRef(XMLStreamWriter writer, String var, String staticVar) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", var);
        writer.writeAttribute("staticVar", staticVar);
    }

    public static void writeMacroStaticRef(XMLStreamWriter writer, String lib) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroStaticRef");
        writer.writeAttribute("id", MACRO_STATIC_REFERENCE_PREFIX + lib);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return lib.equals(((MacroStaticReference) o).lib);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lib);
    }

}

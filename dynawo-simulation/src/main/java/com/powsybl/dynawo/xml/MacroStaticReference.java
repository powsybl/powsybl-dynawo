/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.VarMapping;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.MACRO_STATIC_REFERENCE_PREFIX;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class MacroStaticReference {

    private final String id;
    private final List<VarMapping> varMappings;

    public static MacroStaticReference of(String idSuffix, VarMapping... varMappings) {
        return new MacroStaticReference(MACRO_STATIC_REFERENCE_PREFIX + Objects.requireNonNull(idSuffix),
                List.of(varMappings));
    }

    private MacroStaticReference(String id, List<VarMapping> varMappings) {
        this.id = id;
        this.varMappings = varMappings;
    }

    public String getId() {
        return id;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        if (varMappings.isEmpty()) {
            return;
        }
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        writer.writeAttribute("id", id);
        for (VarMapping varMapping : varMappings) {
            writeVarMapping(writer, varMapping.dynamicVar(), varMapping.staticVar());
        }
        writer.writeEndElement();
    }

    private static void writeVarMapping(XMLStreamWriter writer, String dynamicVar, String staticVar) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", dynamicVar);
        writer.writeAttribute("staticVar", staticVar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return id.equals(((MacroStaticReference) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

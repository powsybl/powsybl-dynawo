/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.par.DynawoParameter;
import com.powsybl.dynawo.par.DynawoParameterSet;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoSolverParameters {

    private DynawoSolverParameters() {
    }

    public static void writeParameterSets(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets)
        throws XMLStreamException {
        for (DynawoParameterSet parameterSet : parameterSets) {
            writeParameterSet(writer, parameterSet);
        }
    }

    private static void writeParameterSet(XMLStreamWriter writer, DynawoParameterSet parameterSet)
        throws XMLStreamException {
        int id = parameterSet.getId();
        writer.writeStartElement("set");
        writer.writeAttribute("id", Integer.toString(id));
        for (DynawoParameter parameter : parameterSet.getParameters()) {
            writeParameter(writer, parameter);
        }
        writer.writeEndElement();
    }

    private static void writeParameter(XMLStreamWriter writer, DynawoParameter parameter) throws XMLStreamException {
        String name = parameter.getName();
        String type = parameter.getType();
        String value = parameter.getValue();
        writeParameter(writer, type, name, value);
    }

    private static void writeParameter(XMLStreamWriter writer, String type, String name, String value)
        throws XMLStreamException {
        writer.writeEmptyElement("par");
        writer.writeAttribute("type", type);
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }
}

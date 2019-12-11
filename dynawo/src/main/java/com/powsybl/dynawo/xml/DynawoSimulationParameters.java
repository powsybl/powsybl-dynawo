/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.par.DynawoParameter;
import com.powsybl.dynawo.par.DynawoParameterRow;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.dynawo.par.DynawoParameterTable;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoSimulationParameters {

    private DynawoSimulationParameters() {
    }

    public static void writeParameterSets(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets)
        throws XMLStreamException {
        for (DynawoParameterSet parameterSet : parameterSets) {
            writeParameterSet(writer, parameterSet);
        }
    }

    private static void writeParameterSet(XMLStreamWriter writer, DynawoParameterSet parameterSet)
        throws XMLStreamException {
        writer.writeStartElement("set");
        writer.writeAttribute("id", parameterSet.getId());
        for (Entry<String, DynawoParameter> parameter : parameterSet.getParameters().entrySet()) {
            writeParameter(writer, parameter.getValue());
        }
        for (DynawoParameterTable parameterTable : parameterSet.getParameterTables()) {
            writeParameterTable(writer, parameterTable);
        }
        writer.writeEndElement();
    }

    private static void writeParameter(XMLStreamWriter writer, DynawoParameter parameter) throws XMLStreamException {
        if (parameter.isReference()) {
            writer.writeEmptyElement("reference");
            writer.writeAttribute("name", parameter.getName());
            writer.writeAttribute("origData", parameter.getOrigData());
            writer.writeAttribute("origName", parameter.getOrigName());
            writer.writeAttribute("type", parameter.getType());
            String componentId = parameter.getComponentId();
            if (componentId != null) {
                writer.writeAttribute("componentId", componentId);
            }
        } else {
            writer.writeEmptyElement("par");
            writer.writeAttribute("type", parameter.getType());
            writer.writeAttribute("name", parameter.getName());
            writer.writeAttribute("value", parameter.getValue());
        }
    }

    private static void writeParameterTable(XMLStreamWriter writer, DynawoParameterTable parameterTable)
        throws XMLStreamException {
        writer.writeStartElement("parTable");
        writer.writeAttribute("type", parameterTable.getType());
        writer.writeAttribute("name", parameterTable.getName());
        for (DynawoParameterRow parameterRow : parameterTable.getParameterRows()) {
            writeParameterRow(writer, parameterRow);
        }
        writer.writeEndElement();
    }

    private static void writeParameterRow(XMLStreamWriter writer, DynawoParameterRow parameterRow)
        throws XMLStreamException {
        writer.writeEmptyElement("row");
        writer.writeAttribute("row", Integer.toString(parameterRow.getRow()));
        writer.writeAttribute("column", Integer.toString(parameterRow.getColumn()));
        writer.writeAttribute("value", parameterRow.getValue());
    }
}

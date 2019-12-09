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
        String id = parameterSet.getId();
        writer.writeStartElement("set");
        writer.writeAttribute("id", id);
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
            String name = parameter.getName();
            String type = parameter.getType();
            String origData = parameter.getOrigData();
            String origName = parameter.getOrigName();
            String componentId = parameter.getComponentId();
            writeReference(writer, name, origData, origName, type, componentId);
        } else {
            String name = parameter.getName();
            String type = parameter.getType();
            String value = parameter.getValue();
            writeParameter(writer, type, name, value);
        }
    }

    private static void writeParameterTable(XMLStreamWriter writer, DynawoParameterTable parameterTable)
        throws XMLStreamException {
        String type = parameterTable.getType();
        String name = parameterTable.getName();
        writer.writeStartElement("parTable");
        writer.writeAttribute("type", type);
        writer.writeAttribute("name", name);
        for (DynawoParameterRow parameterRow : parameterTable.getParameterRows()) {
            writeParameterRow(writer, parameterRow);
        }
        writer.writeEndElement();
    }

    private static void writeParameterRow(XMLStreamWriter writer, DynawoParameterRow parameterRow)
        throws XMLStreamException {
        int row = parameterRow.getRow();
        int column = parameterRow.getColumn();
        String value = parameterRow.getValue();
        writeRow(writer, row, column, value);
    }

    private static void writeRow(XMLStreamWriter writer, int row, int column, String value) throws XMLStreamException {
        writer.writeEmptyElement("row");
        writer.writeAttribute("row", Integer.toString(row));
        writer.writeAttribute("column", Integer.toString(column));
        writer.writeAttribute("value", value);
    }

    private static void writeParameter(XMLStreamWriter writer, String type, String name, String value)
        throws XMLStreamException {
        writer.writeEmptyElement("par");
        writer.writeAttribute("type", type);
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

    private static void writeReference(XMLStreamWriter writer, String name, String origData, String origName,
        String type, String componentId) throws XMLStreamException {
        writer.writeEmptyElement("reference");
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
        writer.writeAttribute("type", type);
        if (componentId != null) {
            writer.writeAttribute("componentId", componentId);
        }
    }
}

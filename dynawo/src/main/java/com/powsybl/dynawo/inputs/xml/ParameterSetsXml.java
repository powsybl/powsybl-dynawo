/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.inputs.model.par.Parameter;
import com.powsybl.dynawo.inputs.model.par.ParameterRow;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;
import com.powsybl.dynawo.inputs.model.par.ParameterTable;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ParameterSetsXml implements DynawoCollectionXmlFile {

    private final String filename;

    public ParameterSetsXml(String filename) {
        this.filename = Objects.requireNonNull(filename);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getCollectionTag() {
        return "parametersSet";
    }

    @Override
    public String getNamespacePrefix() {
        return DynawoXmlConstants.EMPTY_PREFIX;
    }

    @Override
    public void writeCollection(XMLStreamWriter writer, List<?> parameterSets) throws XMLStreamException {
        Objects.requireNonNull(writer);
        for (Object parameterSet : parameterSets) {
            assert parameterSet instanceof ParameterSet;
            writeParameterSet(writer, (ParameterSet) parameterSet);
        }
    }

    private static void writeParameterSet(XMLStreamWriter writer, ParameterSet parameterSet)
        throws XMLStreamException {
        writer.writeStartElement("set");
        writer.writeAttribute("id", parameterSet.getId());
        for (Entry<String, Parameter> parameter : parameterSet.getParameters().entrySet()) {
            writeParameter(writer, parameter.getValue());
        }
        for (ParameterTable parameterTable : parameterSet.getParameterTables()) {
            writeParameterTable(writer, parameterTable);
        }
        writer.writeEndElement();
    }

    private static void writeParameter(XMLStreamWriter writer, Parameter parameter) throws XMLStreamException {
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

    private static void writeParameterTable(XMLStreamWriter writer, ParameterTable parameterTable)
        throws XMLStreamException {
        writer.writeStartElement("parTable");
        writer.writeAttribute("type", parameterTable.getType());
        writer.writeAttribute("name", parameterTable.getName());
        for (ParameterRow parameterRow : parameterTable.getParameterRows()) {
            writeParameterRow(writer, parameterRow);
        }
        writer.writeEndElement();
    }

    private static void writeParameterRow(XMLStreamWriter writer, ParameterRow parameterRow)
        throws XMLStreamException {
        writer.writeEmptyElement("row");
        writer.writeAttribute("row", Integer.toString(parameterRow.getRow()));
        writer.writeAttribute("column", Integer.toString(parameterRow.getColumn()));
        writer.writeAttribute("value", parameterRow.getValue());
    }

}

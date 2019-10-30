/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.DynawoParameterType;
import com.powsybl.dynawo.par.DynawoParameter;
import com.powsybl.dynawo.par.DynawoParameterRow;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.dynawo.par.DynawoParameterTable;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoSimulationParameters {

    private DynawoSimulationParameters() {
    }

    public static int getMaxId(List<DynawoParameterSet> parameterSets) {
        return parameterSets.stream().mapToInt(DynawoParameterSet::getId).max().orElse(1);
    }

    public static void writeParameterSets(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets)
        throws XMLStreamException {
        for (DynawoParameterSet parameterSet : parameterSets) {
            writeParameterSet(writer, parameterSet);
        }
    }

    public static void writeDefaultOmegaRefParameterSets(XMLStreamWriter writer, Network network)
        throws XMLStreamException {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters
            .add(new DynawoParameter("nbGen", DynawoParameterType.INT.getValue(), "" + network.getGeneratorCount()));
        for (int i = 0; i < network.getGeneratorCount(); i++) {
            parameters.add(new DynawoParameter("weight_gen_" + i, DynawoParameterType.DOUBLE.getValue(), "1"));
        }
        DynawoParameterSet parameterSet = new DynawoParameterSet(2);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        writeParameterSet(writer, parameterSet);
    }

    public static void writeDefaultLoad(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets, int setId) throws XMLStreamException {
        writeDefaultParameterSet(writer, parameterSets, setId);
    }

    public static void writeDefaultGenerator(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets, int setId) throws XMLStreamException {
        writeDefaultParameterSet(writer, parameterSets, setId);
    }

    public static void writeDefaultParameterSet(XMLStreamWriter writer, List<DynawoParameterSet> parameterSets, int setId) throws XMLStreamException {
        List<DynawoParameter> parameters = new ArrayList<>();
        for (DynawoParameter parameter : parameterSets.get(0).getParameters()) {
            if (parameter.isReference()) {
                parameters.add(new DynawoParameter(parameter.getName(), parameter.getType(), parameter.getOrigData(), parameter.getOrigName()));
            } else {
                parameters.add(new DynawoParameter(parameter.getName(), parameter.getType(), parameter.getValue()));
            }
        }
        DynawoParameterSet parameterSet = new DynawoParameterSet(setId);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        writeParameterSet(writer, parameterSet);
    }

    private static void writeParameterSet(XMLStreamWriter writer, DynawoParameterSet parameterSet)
        throws XMLStreamException {
        int id = parameterSet.getId();
        writer.writeStartElement("set");
        writer.writeAttribute("id", Integer.toString(id));
        for (DynawoParameter parameter : parameterSet.getParameters()) {
            writeParameter(writer, parameter);
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
            writeReference(writer, name, origData, origName, type);
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
        String type) throws XMLStreamException {
        writer.writeEmptyElement("reference");
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
        writer.writeAttribute("type", type);
    }
}

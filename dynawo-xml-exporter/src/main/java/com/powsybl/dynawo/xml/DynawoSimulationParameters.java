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

    public static int countLoadParameterSets(List<DynawoParameterSet> parameterSets) {
        int loads = 0;
        for (DynawoParameterSet parameterSet : parameterSets) {
            if (parameterSet.getParameters().stream().anyMatch(parameter -> parameter.getName().startsWith("load_"))) {
                loads++;
            }
        }
        return loads;
    }

    public static int countGeneratorParameterSets(List<DynawoParameterSet> parameterSets) {
        int generators = 0;
        for (DynawoParameterSet parameterSet : parameterSets) {
            if (parameterSet.getParameters().stream()
                .anyMatch(parameter -> parameter.getName().startsWith("generator_"))) {
                generators++;
            }
        }
        return generators;
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

    public static boolean hasDefaultOmegaRefParameterSet(List<DynawoParameterSet> parameterSets) {
        return parameterSets.stream().noneMatch(parameterSet -> parameterSet.getParameters().stream()
            .anyMatch(parameter -> parameter.getName().equals("nbGen")));
    }

    public static void writeDefaultLoad(XMLStreamWriter writer, int setId) throws XMLStreamException {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("load_alpha", DynawoParameterType.DOUBLE.getValue(), "1.5"));
        parameters.add(new DynawoParameter("load_beta", DynawoParameterType.DOUBLE.getValue(), "2.5"));
        parameters.add(new DynawoParameter("load_P0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new DynawoParameter("load_Q0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new DynawoParameter("load_U0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new DynawoParameter("load_UPhase0", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "angle_pu"));
        DynawoParameterSet parameterSet = new DynawoParameterSet(setId);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        writeParameterSet(writer, parameterSet);
    }

    public static void writeDefaultGenerator(XMLStreamWriter writer, int setId) throws XMLStreamException {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("generator_ExcitationPu", DynawoParameterType.INT.getValue(), "1"));
        parameters.add(new DynawoParameter("generator_DPu", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("generator_H", DynawoParameterType.DOUBLE.getValue(), "5.4000000000000004"));
        parameters
            .add(new DynawoParameter("generator_RaPu", DynawoParameterType.DOUBLE.getValue(), "0.0027959999999999999"));
        parameters
            .add(new DynawoParameter("generator_XlPu", DynawoParameterType.DOUBLE.getValue(), "0.20200000000000001"));
        parameters
            .add(new DynawoParameter("generator_XdPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters
            .add(new DynawoParameter("generator_XpdPu", DynawoParameterType.DOUBLE.getValue(), "0.38400000000000001"));
        parameters
            .add(new DynawoParameter("generator_XppdPu", DynawoParameterType.DOUBLE.getValue(), "0.26400000000000001"));
        parameters
            .add(new DynawoParameter("generator_Tpd0", DynawoParameterType.DOUBLE.getValue(), "8.0939999999999994"));
        parameters
            .add(new DynawoParameter("generator_Tppd0", DynawoParameterType.DOUBLE.getValue(), "0.080000000000000002"));
        parameters
            .add(new DynawoParameter("generator_XqPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters
            .add(new DynawoParameter("generator_XpqPu", DynawoParameterType.DOUBLE.getValue(), "0.39300000000000002"));
        parameters
            .add(new DynawoParameter("generator_XppqPu", DynawoParameterType.DOUBLE.getValue(), "0.26200000000000001"));
        parameters
            .add(new DynawoParameter("generator_Tpq0", DynawoParameterType.DOUBLE.getValue(), "1.5720000000000001"));
        parameters
            .add(new DynawoParameter("generator_Tppq0", DynawoParameterType.DOUBLE.getValue(), "0.084000000000000005"));
        parameters.add(new DynawoParameter("generator_UNom", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new DynawoParameter("generator_SNom", DynawoParameterType.DOUBLE.getValue(), "1211"));
        parameters.add(new DynawoParameter("generator_PNom", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new DynawoParameter("generator_SnTfo", DynawoParameterType.DOUBLE.getValue(), "1211"));
        parameters.add(new DynawoParameter("generator_UNomHV", DynawoParameterType.DOUBLE.getValue(), "69"));
        parameters.add(new DynawoParameter("generator_UNomLV", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new DynawoParameter("generator_UBaseHV", DynawoParameterType.DOUBLE.getValue(), "69"));
        parameters.add(new DynawoParameter("generator_UBaseLV", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new DynawoParameter("generator_RTfPu", DynawoParameterType.DOUBLE.getValue(), "0.0"));
        parameters.add(new DynawoParameter("generator_XTfPu", DynawoParameterType.DOUBLE.getValue(), "0.1"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMax", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMin", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMinPu", DynawoParameterType.DOUBLE.getValue(), "-5"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMaxPu", DynawoParameterType.DOUBLE.getValue(), "5"));
        parameters
            .add(new DynawoParameter("voltageRegulator_UsRefMinPu", DynawoParameterType.DOUBLE.getValue(), "0.8"));
        parameters
            .add(new DynawoParameter("voltageRegulator_UsRefMaxPu", DynawoParameterType.DOUBLE.getValue(), "1.2"));
        parameters.add(new DynawoParameter("voltageRegulator_Gain", DynawoParameterType.DOUBLE.getValue(), "20"));
        parameters.add(new DynawoParameter("governor_KGover", DynawoParameterType.DOUBLE.getValue(), "5"));
        parameters.add(new DynawoParameter("governor_PMin", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("governor_PMax", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new DynawoParameter("governor_PNom", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new DynawoParameter("URef_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("Pm_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("generator_P0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new DynawoParameter("generator_Q0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new DynawoParameter("generator_U0Pu", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new DynawoParameter("generator_UPhase0", DynawoParameterType.DOUBLE.getValue(),
            DynawoParameterType.IIDM.getValue(), "angle_pu"));
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

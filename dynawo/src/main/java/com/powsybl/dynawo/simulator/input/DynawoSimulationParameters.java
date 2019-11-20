/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoSimulationParameters {

    private static final String DOUBLE = "DOUBLE";
    private static final String BOOLEAN = "BOOL";
    private static final String IIDM = "IIDM";

    private DynawoSimulationParameters() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeStartSet(XMLStreamWriter writer, int id) throws XMLStreamException {
        writer.writeStartElement("set");
        writer.writeAttribute("id", Integer.toString(id));
    }

    public static void writeEndSet(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    public static void writeOmegaRefParameters(Network network, XMLStreamWriter writer) throws XMLStreamException {
        writeParameter(writer, "INT", "nbGen", "" + network.getGeneratorCount());
        writeParameter(writer, DOUBLE, "weight_gen_0", "1211");
        writeParameter(writer, DOUBLE, "weight_gen_1", "1120");
        writeParameter(writer, DOUBLE, "weight_gen_2", "1650");
        writeParameter(writer, DOUBLE, "weight_gen_3", "80");
        writeParameter(writer, DOUBLE, "weight_gen_4", "250");
        for (int i = 5; i < network.getGeneratorCount(); i++) {
            writeParameter(writer, DOUBLE, "weight_gen_" + i, "1");
        }
    }

    public static void writeGlobalParameters(XMLStreamWriter writer) throws XMLStreamException {
        writeParameter(writer, DOUBLE, "capacitor_no_reclosing_delay", "300");
        writeParameter(writer, DOUBLE, "dangling_line_currentLimit_maxTimeOperation", "90");
        writeParameter(writer, DOUBLE, "line_currentLimit_maxTimeOperation", "90");
        writeParameter(writer, DOUBLE, "load_Tp", "90");
        writeParameter(writer, DOUBLE, "load_Tq", "90");
        writeParameter(writer, DOUBLE, "load_alpha", "1");
        writeParameter(writer, DOUBLE, "load_alphaLong", "0");
        writeParameter(writer, DOUBLE, "load_beta", "2");
        writeParameter(writer, DOUBLE, "load_betaLong", "0");
        writeParameter(writer, BOOLEAN, "load_isControllable", "false");
        writeParameter(writer, BOOLEAN, "load_isRestorative", "false");
        writeParameter(writer, DOUBLE, "load_zPMax", "100");
        writeParameter(writer, DOUBLE, "load_zQMax", "100");
        writeParameter(writer, DOUBLE, "reactance_no_reclosing_delay", "0");
        writeParameter(writer, DOUBLE, "transformer_currentLimit_maxTimeOperation", "90");
        writeParameter(writer, DOUBLE, "transformer_t1st_HT", "60");
        writeParameter(writer, DOUBLE, "transformer_t1st_THT", "30");
        writeParameter(writer, DOUBLE, "transformer_tNext_HT", "10");
        writeParameter(writer, DOUBLE, "transformer_tNext_THT", "10");
        writeParameter(writer, DOUBLE, "transformer_tolV", "0.014999999700000001");
    }

    public static void writeEventParameters(XMLStreamWriter writer) throws XMLStreamException {
        writeParameter(writer, DOUBLE, "event_tEvent", "1");
        writeParameter(writer, BOOLEAN, "event_disconnectOrigin", "false");
        writeParameter(writer, BOOLEAN, "event_disconnectExtremity", "true");
    }

    public static void writeLoadParameters(XMLStreamWriter writer) throws XMLStreamException {
        writeParameter(writer, DOUBLE, "load_alpha", "1.5");
        writeParameter(writer, DOUBLE, "load_beta", "2.5");
        writeReference(writer, "load_P0Pu", IIDM, "p_pu", DOUBLE);
        writeReference(writer, "load_Q0Pu", IIDM, "q_pu", DOUBLE);
        writeReference(writer, "load_U0Pu", IIDM, "v_pu", DOUBLE);
        writeReference(writer, "load_UPhase0", IIDM, "angle_pu", DOUBLE);
    }

    public static void writeGeneratorParameters(XMLStreamWriter writer) throws XMLStreamException {
        writeParameter(writer, "INT", "generator_ExcitationPu", "1");
        writeParameter(writer, DOUBLE, "generator_DPu", "0");
        writeParameter(writer, DOUBLE, "generator_H", "5.4000000000000004");
        writeParameter(writer, DOUBLE, "generator_RaPu", "0.0027959999999999999");
        writeParameter(writer, DOUBLE, "generator_XlPu", "0.20200000000000001");
        writeParameter(writer, DOUBLE, "generator_XdPu", "2.2200000000000002");
        writeParameter(writer, DOUBLE, "generator_XpdPu", "0.38400000000000001");
        writeParameter(writer, DOUBLE, "generator_XppdPu", "0.26400000000000001");
        writeParameter(writer, DOUBLE, "generator_Tpd0", "8.0939999999999994");
        writeParameter(writer, DOUBLE, "generator_Tppd0", "0.080000000000000002");
        writeParameter(writer, DOUBLE, "generator_XqPu", "2.2200000000000002");
        writeParameter(writer, DOUBLE, "generator_XpqPu", "0.39300000000000002");
        writeParameter(writer, DOUBLE, "generator_XppqPu", "0.26200000000000001");
        writeParameter(writer, DOUBLE, "generator_Tpq0", "1.5720000000000001");
        writeParameter(writer, DOUBLE, "generator_Tppq0", "0.084000000000000005");
        writeParameter(writer, DOUBLE, "generator_UNom", "24");
        writeParameter(writer, DOUBLE, "generator_SNom", "1211");
        writeParameter(writer, DOUBLE, "generator_PNom", "1090");
        writeParameter(writer, DOUBLE, "generator_SnTfo", "1211");
        writeParameter(writer, DOUBLE, "generator_UNomHV", "69");
        writeParameter(writer, DOUBLE, "generator_UNomLV", "24");
        writeParameter(writer, DOUBLE, "generator_UBaseHV", "69");
        writeParameter(writer, DOUBLE, "generator_UBaseLV", "24");
        writeParameter(writer, DOUBLE, "generator_RTfPu", "0.0");
        writeParameter(writer, DOUBLE, "generator_XTfPu", "0.1");
        writeParameter(writer, DOUBLE, "voltageRegulator_LagEfdMax", "0");
        writeParameter(writer, DOUBLE, "voltageRegulator_LagEfdMin", "0");
        writeParameter(writer, DOUBLE, "voltageRegulator_EfdMinPu", "-5");
        writeParameter(writer, DOUBLE, "voltageRegulator_EfdMaxPu", "5");
        writeParameter(writer, DOUBLE, "voltageRegulator_Gain", "20");
        writeParameter(writer, DOUBLE, "governor_KGover", "5");
        writeParameter(writer, DOUBLE, "governor_PMin", "0");
        writeParameter(writer, DOUBLE, "governor_PMax", "1090");
        writeParameter(writer, DOUBLE, "governor_PNom", "1090");
        writeParameter(writer, DOUBLE, "URef_ValueIn", "0");
        writeParameter(writer, DOUBLE, "Pm_ValueIn", "0");
        writeReference(writer, "generator_P0Pu", IIDM, "p_pu", DOUBLE);
        writeReference(writer, "generator_Q0Pu", IIDM, "q_pu", DOUBLE);
        writeReference(writer, "generator_U0Pu", IIDM, "v_pu", DOUBLE);
        writeReference(writer, "generator_UPhase0", IIDM, "angle_pu", DOUBLE);
    }

    private static void writeParameter(XMLStreamWriter writer, String type, String name, String value)
        throws XMLStreamException {
        writer.writeEmptyElement("par");
        writer.writeAttribute("type", type);
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

    private static void writeReference(XMLStreamWriter writer, String name, String origData, String origName, String type)
        throws XMLStreamException {
        writer.writeEmptyElement("reference");
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
        writer.writeAttribute("type", type);
    }
}

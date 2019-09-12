/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters {

    private static final String DOUBLE = "DOUBLE";
    private static final String BOOLEAN = "BOOL";
    private static final String IIDM = "IIDM";

    public DynawoSimulationParameters(Network network) {
        this.network = network;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameters()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.par");
        }
    }

    private CharSequence parameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        int id = 1;
        openSet(builder, id++);
        globalParameters(builder);
        closeSet(builder);
        for (Load l : network.getLoads()) {
            openSet(builder, id++);
            loadParameters(builder);
            closeSet(builder);
        }
        for (Generator g : network.getGenerators()) {
            openSet(builder, id++);
            genParameters(builder);
            closeSet(builder);
        }
        openSet(builder, id++);
        omegaRefParameters(builder);
        closeSet(builder);
        openSet(builder, id);
        eventParameters(builder);
        closeSet(builder);
        builder.append(String.join(System.lineSeparator(),
            "</parametersSet>") + System.lineSeparator());
        return builder.toString();
    }

    private void openSet(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "  <set id=\"" + id + "\">") + System.lineSeparator());
    }

    private void closeSet(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "  </set>") + System.lineSeparator());
    }

    private void omegaRefParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setParameter("INT", "nbGen", "" + network.getGeneratorCount()),
            setParameter(DOUBLE, "weight_gen_0", "1211"),
            setParameter(DOUBLE, "weight_gen_1", "1120"),
            setParameter(DOUBLE, "weight_gen_2", "1650"),
            setParameter(DOUBLE, "weight_gen_3", "80"),
            setParameter(DOUBLE, "weight_gen_4", "250")) + System.lineSeparator());
        for (int i = 5; i < network.getGeneratorCount(); i++) {
            builder.append(String.join(System.lineSeparator(),
                setParameter(DOUBLE, "weight_gen_" + i, "1")) + System.lineSeparator());
        }
    }

    private void globalParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setParameter(DOUBLE, "capacitor_no_reclosing_delay", "300"),
            setParameter(DOUBLE, "dangling_line_currentLimit_maxTimeOperation", "90"),
            setParameter(DOUBLE, "line_currentLimit_maxTimeOperation", "90"),
            setParameter(DOUBLE, "load_Tp", "90"),
            setParameter(DOUBLE, "load_Tq", "90"),
            setParameter(DOUBLE, "load_alpha", "1"),
            setParameter(DOUBLE, "load_alphaLong", "0"),
            setParameter(DOUBLE, "load_beta", "2"),
            setParameter(DOUBLE, "load_betaLong", "0"),
            setParameter(BOOLEAN, "load_isControllable", "false"),
            setParameter(BOOLEAN, "load_isRestorative", "false"),
            setParameter(DOUBLE, "load_zPMax", "100"),
            setParameter(DOUBLE, "load_zQMax", "100"),
            setParameter(DOUBLE, "reactance_no_reclosing_delay", "0"),
            setParameter(DOUBLE, "transformer_currentLimit_maxTimeOperation", "90"),
            setParameter(DOUBLE, "transformer_t1st_HT", "60"),
            setParameter(DOUBLE, "transformer_t1st_THT", "30"),
            setParameter(DOUBLE, "transformer_tNext_HT", "10"),
            setParameter(DOUBLE, "transformer_tNext_THT", "10"),
            setParameter(DOUBLE, "transformer_tolV", "0.014999999700000001")) + System.lineSeparator());
    }

    private void eventParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setParameter(DOUBLE, "event_tEvent", "1"),
            setParameter(BOOLEAN, "event_disconnectOrigin", "false"),
            setParameter(BOOLEAN, "event_disconnectExtremity", "true")) + System.lineSeparator());
    }

    private void loadParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setParameter(DOUBLE, "load_alpha", "1.5"),
            setParameter(DOUBLE, "load_beta", "2.5"),
            setReference("load_P0Pu", IIDM, "p_pu", DOUBLE),
            setReference("load_Q0Pu", IIDM, "q_pu", DOUBLE),
            setReference("load_U0Pu", IIDM, "v_pu", DOUBLE),
            setReference("load_UPhase0", IIDM, "angle_pu", DOUBLE)) + System.lineSeparator());
    }

    private void genParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setParameter("INT", "generator_ExcitationPu", "1"),
            setParameter(DOUBLE, "generator_DPu", "0"),
            setParameter(DOUBLE, "generator_H", "5.4000000000000004"),
            setParameter(DOUBLE, "generator_RaPu", "0.0027959999999999999"),
            setParameter(DOUBLE, "generator_XlPu", "0.20200000000000001"),
            setParameter(DOUBLE, "generator_XdPu", "2.2200000000000002"),
            setParameter(DOUBLE, "generator_XpdPu", "0.38400000000000001"),
            setParameter(DOUBLE, "generator_XppdPu", "0.26400000000000001"),
            setParameter(DOUBLE, "generator_Tpd0", "8.0939999999999994"),
            setParameter(DOUBLE, "generator_Tppd0", "0.080000000000000002"),
            setParameter(DOUBLE, "generator_XqPu", "2.2200000000000002"),
            setParameter(DOUBLE, "generator_XpqPu", "0.39300000000000002"),
            setParameter(DOUBLE, "generator_XppqPu", "0.26200000000000001"),
            setParameter(DOUBLE, "generator_Tpq0", "1.5720000000000001"),
            setParameter(DOUBLE, "generator_Tppq0", "0.084000000000000005"),
            setParameter(DOUBLE, "generator_UNom", "24"),
            setParameter(DOUBLE, "generator_SNom", "1211"),
            setParameter(DOUBLE, "generator_PNom", "1090"),
            setParameter(DOUBLE, "generator_SnTfo", "1211"),
            setParameter(DOUBLE, "generator_UNomHV", "69"),
            setParameter(DOUBLE, "generator_UNomLV", "24"),
            setParameter(DOUBLE, "generator_UBaseHV", "69"),
            setParameter(DOUBLE, "generator_UBaseLV", "24"),
            setParameter(DOUBLE, "generator_RTfPu", "0.0"),
            setParameter(DOUBLE, "generator_XTfPu", "0.1"),
            setParameter(DOUBLE, "voltageRegulator_LagEfdMax", "0"),
            setParameter(DOUBLE, "voltageRegulator_LagEfdMin", "0"),
            setParameter(DOUBLE, "voltageRegulator_EfdMinPu", "-5"),
            setParameter(DOUBLE, "voltageRegulator_EfdMaxPu", "5"),
            setParameter(DOUBLE, "voltageRegulator_Gain", "20"),
            setParameter(DOUBLE, "governor_KGover", "5"),
            setParameter(DOUBLE, "governor_PMin", "0"),
            setParameter(DOUBLE, "governor_PMax", "1090"),
            setParameter(DOUBLE, "governor_PNom", "1090"),
            setParameter(DOUBLE, "URef_ValueIn", "0"),
            setParameter(DOUBLE, "Pm_ValueIn", "0"),
            setReference("generator_P0Pu", IIDM, "p_pu", DOUBLE),
            setReference("generator_Q0Pu", IIDM, "q_pu", DOUBLE),
            setReference("generator_U0Pu", IIDM, "v_pu", DOUBLE),
            setReference("generator_UPhase0", IIDM, "angle_pu", DOUBLE)) + System.lineSeparator());
    }

    private String setParameter(String type, String name, String value) {
        return "    <par type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    private String setReference(String name, String origData, String origName, String type) {
        return "    <reference name=\"" + name + "\" origData=\"" + origData + "\" origName=\"" + origName + "\" type=\"" + type + "\"/>";
    }

    private final Network network;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationParameters.class);
}

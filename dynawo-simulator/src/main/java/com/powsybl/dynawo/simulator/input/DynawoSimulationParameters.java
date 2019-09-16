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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynawo.DynawoParameter;
import com.powsybl.dynawo.DynawoParameterSet;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters {

    public DynawoSimulationParameters(Network network, DynawoProvider provider) {
        this.network = network;
        this.parameterSets = provider.getDynawoParameterSets(network);
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), writeParameterSets()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.par");
        }
    }

    private String writeParameterSets() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.writeInputHeader(),
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        if (parameterSets.stream().noneMatch(this::defaultOmegaRefParameterSet)) {
            builder.append(
                String.join(System.lineSeparator(), writeDefaultOmegaRefParameterSets()) + System.lineSeparator());
        }
        parameterSets.forEach(parameterSet -> builder
            .append(String.join(System.lineSeparator(), writeParameterSet(parameterSet)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(), writeDefaultParameterSets()) + System.lineSeparator());
        builder.append(String.join(System.lineSeparator(),
            "</parametersSet>") + System.lineSeparator());
        return builder.toString();
    }

    private String writeDefaultOmegaRefParameterSets() {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("nbGen", DynawoInput.INT, "" + network.getGeneratorCount()));
        for (int i = 0; i < network.getGeneratorCount(); i++) {
            parameters.add(new DynawoParameter("weight_gen_" + i, DynawoInput.DOUBLE, "1"));
        }
        parameterSets.add(new DynawoParameterSet(2, Collections.unmodifiableList(parameters)));
        return writeParameterSet(new DynawoParameterSet(2, parameters));
    }

    private boolean defaultOmegaRefParameterSet(DynawoParameterSet parameterSet) {
        return parameterSet.getParameters().stream().anyMatch(parameter -> parameter.getName().equals("nbGen"));
    }

    private String writeDefaultParameterSets() {
        StringBuilder builder = new StringBuilder();
        while (loads <= network.getLoadCount()) {
            builder.append(String.join(System.lineSeparator(),
                writeDefaultLoad()) + System.lineSeparator());
        }
        while (generators <= network.getGeneratorCount()) {
            builder.append(String.join(System.lineSeparator(), writeDefaultGenerator())
                + System.lineSeparator());
        }
        return builder.toString();
    }

    private CharSequence writeDefaultLoad() {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("load_alpha", DynawoInput.DOUBLE, "1.5"));
        parameters.add(new DynawoParameter("load_beta", DynawoInput.DOUBLE, "2.5"));
        parameters.add(new DynawoParameter("load_P0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "p_pu"));
        parameters.add(new DynawoParameter("load_Q0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "q_pu"));
        parameters.add(new DynawoParameter("load_U0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "v_pu"));
        parameters.add(new DynawoParameter("load_UPhase0", DynawoInput.DOUBLE, DynawoInput.IIDM, "angle_pu"));
        return writeParameterSet(new DynawoParameterSet(maxSetId + 1, parameters));
    }

    private CharSequence writeDefaultGenerator() {
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("generator_ExcitationPu", DynawoInput.INT, "1"));
        parameters.add(new DynawoParameter("generator_DPu", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("generator_H", DynawoInput.DOUBLE, "5.4000000000000004"));
        parameters.add(new DynawoParameter("generator_RaPu", DynawoInput.DOUBLE, "0.0027959999999999999"));
        parameters.add(new DynawoParameter("generator_XlPu", DynawoInput.DOUBLE, "0.20200000000000001"));
        parameters.add(new DynawoParameter("generator_XdPu", DynawoInput.DOUBLE, "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpdPu", DynawoInput.DOUBLE, "0.38400000000000001"));
        parameters.add(new DynawoParameter("generator_XppdPu", DynawoInput.DOUBLE, "0.26400000000000001"));
        parameters.add(new DynawoParameter("generator_Tpd0", DynawoInput.DOUBLE, "8.0939999999999994"));
        parameters.add(new DynawoParameter("generator_Tppd0", DynawoInput.DOUBLE, "0.080000000000000002"));
        parameters.add(new DynawoParameter("generator_XqPu", DynawoInput.DOUBLE, "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpqPu", DynawoInput.DOUBLE, "0.39300000000000002"));
        parameters.add(new DynawoParameter("generator_XppqPu", DynawoInput.DOUBLE, "0.26200000000000001"));
        parameters.add(new DynawoParameter("generator_Tpq0", DynawoInput.DOUBLE, "1.5720000000000001"));
        parameters.add(new DynawoParameter("generator_Tppq0", DynawoInput.DOUBLE, "0.084000000000000005"));
        parameters.add(new DynawoParameter("generator_UNom", DynawoInput.DOUBLE, "24"));
        parameters.add(new DynawoParameter("generator_SNom", DynawoInput.DOUBLE, "1211"));
        parameters.add(new DynawoParameter("generator_PNom", DynawoInput.DOUBLE, "1090"));
        parameters.add(new DynawoParameter("generator_SnTfo", DynawoInput.DOUBLE, "1211"));
        parameters.add(new DynawoParameter("generator_UNomHV", DynawoInput.DOUBLE, "69"));
        parameters.add(new DynawoParameter("generator_UNomLV", DynawoInput.DOUBLE, "24"));
        parameters.add(new DynawoParameter("generator_UBaseHV", DynawoInput.DOUBLE, "69"));
        parameters.add(new DynawoParameter("generator_UBaseLV", DynawoInput.DOUBLE, "24"));
        parameters.add(new DynawoParameter("generator_RTfPu", DynawoInput.DOUBLE, "0.0"));
        parameters.add(new DynawoParameter("generator_XTfPu", DynawoInput.DOUBLE, "0.1"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMax", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMin", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMinPu", DynawoInput.DOUBLE, "-5"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMaxPu", DynawoInput.DOUBLE, "5"));
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMinPu", DynawoInput.DOUBLE, "0.8"));
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMaxPu", DynawoInput.DOUBLE, "1.2"));
        parameters.add(new DynawoParameter("voltageRegulator_Gain", DynawoInput.DOUBLE, "20"));
        parameters.add(new DynawoParameter("governor_KGover", DynawoInput.DOUBLE, "5"));
        parameters.add(new DynawoParameter("governor_PMin", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("governor_PMax", DynawoInput.DOUBLE, "1090"));
        parameters.add(new DynawoParameter("governor_PNom", DynawoInput.DOUBLE, "1090"));
        parameters.add(new DynawoParameter("URef_ValueIn", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("Pm_ValueIn", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("generator_P0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "p_pu"));
        parameters.add(new DynawoParameter("generator_Q0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "q_pu"));
        parameters.add(new DynawoParameter("generator_U0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "v_pu"));
        parameters.add(new DynawoParameter("generator_UPhase0", DynawoInput.DOUBLE, DynawoInput.IIDM, "angle_pu"));
        return writeParameterSet(new DynawoParameterSet(maxSetId + 1, parameters));
    }

    private String writeParameterSet(DynawoParameterSet parameterSet) {
        StringBuilder builder = new StringBuilder();
        int id = parameterSet.getId();
        if (id > maxSetId) {
            maxSetId = id;
        }
        builder.append(String.join(System.lineSeparator(),
            "  <set id=\"" + id + "\">") + System.lineSeparator());
        parameterSet.getParameters().forEach(parameter -> builder
            .append(String.join(System.lineSeparator(), writeParameter(parameter)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "  </set>") + System.lineSeparator());
        countParameterSetByType(parameterSet);
        return builder.toString();
    }

    private void countParameterSetByType(DynawoParameterSet parameterSet) {
        if (parameterSet.getParameters().stream().anyMatch(parameter -> parameter.getName().startsWith("load_"))) {
            loads++;
        } else if (parameterSet.getParameters().stream()
            .anyMatch(parameter -> parameter.getName().startsWith("generator_"))) {
            generators++;
        }
    }

    private String writeParameter(DynawoParameter parameter) {
        if (parameter.isReference()) {
            String name = parameter.getName();
            String type = parameter.getType();
            String origData = parameter.getOrigData();
            String origName = parameter.getOrigName();
            return writeReference(name, origData, origName, type);
        } else {
            String name = parameter.getName();
            String type = parameter.getType();
            String value = parameter.getValue();
            return writeParameter(type, name, value);
        }
    }

    private String writeParameter(String type, String name, String value) {
        return "    <par type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    private String writeReference(String name, String origData, String origName, String type) {
        return "    <reference name=\"" + name + "\" origData=\"" + origData + "\" origName=\"" + origName
            + "\" type=\"" + type + "\"/>";
    }

    private final List<DynawoParameterSet> parameterSets;
    private final Network network;
    private int maxSetId = 1;
    private int loads = 0;
    private int generators = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationParameters.class);
}

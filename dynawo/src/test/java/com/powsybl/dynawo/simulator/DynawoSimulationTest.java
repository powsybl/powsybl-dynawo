/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.inputs.dsl.GroovyDslDynawoInputProvider;
import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.dynawo.inputs.model.DynawoParameterType;
import com.powsybl.dynawo.inputs.model.crv.Curve;
import com.powsybl.dynawo.inputs.model.dyd.BlackBoxModel;
import com.powsybl.dynawo.inputs.model.dyd.Connection;
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.job.Modeler;
import com.powsybl.dynawo.inputs.model.job.Outputs;
import com.powsybl.dynawo.inputs.model.job.Simulation;
import com.powsybl.dynawo.inputs.model.job.Solver;
import com.powsybl.dynawo.inputs.model.job.LogAppender;
import com.powsybl.dynawo.inputs.model.par.Parameter;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;
import com.powsybl.dynawo.results.DynawoResults;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationTest {

    @Test
    public void test() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {

            PlatformConfig platformConfig = configure(fs);
            DynawoSimulationTester tester = new DynawoSimulationTester(true);
            Network network = tester.convert(platformConfig, Cim14SmallCasesCatalog.nordic32());
            DynawoInputs dynawoInputs = buildInputs(network);
            DynawoResults result = tester.simulate(network, dynawoInputs, platformConfig);
            assertTrue(result.isOk());
            assertNull(result.getLogs());

            // check final voltage of bus close to the event
            int index = result.getTimeSeries().getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSeries().getValues().get(new Double(30.0)).get(index), new Double(0.931558));
        }
    }

    @Test
    public void testGroovy() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {

            PlatformConfig platformConfig = configure(fs);
            DynawoSimulationTester tester = new DynawoSimulationTester(true);
            Network network = tester.convert(platformConfig, Cim14SmallCasesCatalog.nordic32());
            DynawoInputs dynawoInputs = new GroovyDslDynawoInputProvider(getClass().getResourceAsStream("/nordic32/nordic32.groovy")).getDynawoInputs(network);
            DynawoResults result = tester.simulate(network, dynawoInputs, platformConfig);

            // check final voltage of bus close to the event
            int index = result.getTimeSeries().getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSeries().getValues().get(new Double(30.0)).get(index), new Double(0.931558));
        }
    }

    private PlatformConfig configure(FileSystem fs) throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fs);
        Files.createDirectories(fs.getPath("/tmp"));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("homeDir", "/home/dynawo");
        moduleConfig.setStringProperty("debug", "false");
        moduleConfig = platformConfig.createModuleConfig("import-export-parameters-default-value");
        moduleConfig.setStringProperty("iidm.export.xml.extensions", "null");
        moduleConfig = platformConfig.createModuleConfig("computation-local");
        moduleConfig.setStringProperty("tmpDir", "/tmp");
        return platformConfig;
    }

    private DynawoInputs buildInputs(Network network) {

        DynawoInputs dynawoInputs = Mockito.mock(DynawoInputs.class);

        // Job file
        Solver solver = new Solver("dynawo_SolverIDA", "solvers.par", "2");
        Modeler modeler = new Modeler("outputs/compilation", "powsybl_network.xiidm", "powsybl_dynawo.par", "1",
            "powsybl_dynawo.dyd");
        Simulation simulation = new Simulation(0, 30, false, 1e-6);
        Outputs outputs = new Outputs("outputs", "powsybl_dynawo.crv");
        outputs.add(new LogAppender("", "dynawo.log", "DEBUG"));
        outputs.add(new LogAppender("COMPILE", "dynawoCompiler.log", "DEBUG"));
        outputs.add(new LogAppender("MODELER", "dynawoModeler.log", "DEBUG"));
        Job job = new Job("Nordic 32 - Disconnect Line", solver, modeler, simulation, outputs);
        Mockito.when(dynawoInputs.getJobs()).thenReturn(Collections.singletonList(job));

        // Solvers file
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("order", DynawoParameterType.INT.getValue(), "2"));
        parameters.add(new Parameter("initStep", DynawoParameterType.DOUBLE.getValue(), "0.000001"));
        parameters.add(new Parameter("minStep", DynawoParameterType.DOUBLE.getValue(), "0.000001"));
        parameters.add(new Parameter("maxStep", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new Parameter("absAccuracy", DynawoParameterType.DOUBLE.getValue(), "1e-4"));
        parameters.add(new Parameter("relAccuracy", DynawoParameterType.DOUBLE.getValue(), "1e-4"));
        ParameterSet solverParams = new ParameterSet("2");
        solverParams.addParameters(Collections.unmodifiableList(parameters));
        Mockito.when(dynawoInputs.getSolverParameterSets())
            .thenReturn(Collections.singletonList(solverParams));

        // Parameters file
        List<ParameterSet> parameterSets = new ArrayList<>();
        // Global param
        parameters = new ArrayList<>();
        parameters.add(new Parameter("capacitor_no_reclosing_delay", DynawoParameterType.DOUBLE.getValue(), "300"));
        parameters.add(new Parameter("dangling_line_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new Parameter("line_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new Parameter("load_Tp", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new Parameter("load_Tq", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new Parameter("load_alpha", DynawoParameterType.DOUBLE.getValue(), "1"));
        parameters.add(new Parameter("load_alphaLong", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("load_beta", DynawoParameterType.DOUBLE.getValue(), "2"));
        parameters.add(new Parameter("load_betaLong", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("load_isControllable", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new Parameter("load_isRestorative", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new Parameter("load_zPMax", DynawoParameterType.DOUBLE.getValue(), "100"));
        parameters.add(new Parameter("load_zQMax", DynawoParameterType.DOUBLE.getValue(), "100"));
        parameters.add(new Parameter("reactance_no_reclosing_delay", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("transformer_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new Parameter("transformer_t1st_HT", DynawoParameterType.DOUBLE.getValue(), "60"));
        parameters.add(new Parameter("transformer_t1st_THT", DynawoParameterType.DOUBLE.getValue(), "30"));
        parameters.add(new Parameter("transformer_tNext_HT", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new Parameter("transformer_tNext_THT", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new Parameter("transformer_tolV", DynawoParameterType.DOUBLE.getValue(), "0.014999999700000001"));
        ParameterSet parameterSet = new ParameterSet("1");
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Omega Ref param
        parameters = new ArrayList<>();
        parameters.add(new Parameter("nbGen", DynawoParameterType.INT.getValue(), "" + network.getGeneratorCount()));
        parameters.add(new Parameter("weight_gen_0", DynawoParameterType.DOUBLE.getValue(), "1211"));
        for (int i = 1; i < network.getGeneratorCount(); i++) {
            parameters.add(new Parameter("weight_gen_" + i, DynawoParameterType.DOUBLE.getValue(), "1"));
        }
        parameterSet = new ParameterSet("2");
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Load param
        parameters = new ArrayList<>();
        parameters.add(new Parameter("load_alpha", DynawoParameterType.DOUBLE.getValue(), "1.5"));
        parameters.add(new Parameter("load_beta", DynawoParameterType.DOUBLE.getValue(), "2.5"));
        parameters.add(new Parameter("load_P0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new Parameter("load_Q0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new Parameter("load_U0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new Parameter("load_UPhase0", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "angle_pu"));
        parameterSet = new ParameterSet("3");
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Generator param
        parameters = new ArrayList<>();
        parameters.add(new Parameter("generator_ExcitationPu", DynawoParameterType.INT.getValue(), "1"));
        parameters.add(new Parameter("generator_DPu", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("generator_H", DynawoParameterType.DOUBLE.getValue(), "5.4000000000000004"));
        parameters.add(new Parameter("generator_RaPu", DynawoParameterType.DOUBLE.getValue(), "0.0027959999999999999"));
        parameters.add(new Parameter("generator_XlPu", DynawoParameterType.DOUBLE.getValue(), "0.20200000000000001"));
        parameters.add(new Parameter("generator_XdPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters.add(new Parameter("generator_XpdPu", DynawoParameterType.DOUBLE.getValue(), "0.38400000000000001"));
        parameters.add(new Parameter("generator_XppdPu", DynawoParameterType.DOUBLE.getValue(), "0.26400000000000001"));
        parameters.add(new Parameter("generator_Tpd0", DynawoParameterType.DOUBLE.getValue(), "8.0939999999999994"));
        parameters.add(new Parameter("generator_Tppd0", DynawoParameterType.DOUBLE.getValue(), "0.080000000000000002"));
        parameters.add(new Parameter("generator_XqPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters.add(new Parameter("generator_XpqPu", DynawoParameterType.DOUBLE.getValue(), "0.39300000000000002"));
        parameters.add(new Parameter("generator_XppqPu", DynawoParameterType.DOUBLE.getValue(), "0.26200000000000001"));
        parameters.add(new Parameter("generator_Tpq0", DynawoParameterType.DOUBLE.getValue(), "1.5720000000000001"));
        parameters.add(new Parameter("generator_Tppq0", DynawoParameterType.DOUBLE.getValue(), "0.084000000000000005"));
        parameters.add(new Parameter("generator_UNom", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new Parameter("generator_SNom", DynawoParameterType.DOUBLE.getValue(), "1211"));
        parameters.add(new Parameter("generator_PNom", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new Parameter("generator_SnTfo", DynawoParameterType.DOUBLE.getValue(), "1211"));
        parameters.add(new Parameter("generator_UNomHV", DynawoParameterType.DOUBLE.getValue(), "69"));
        parameters.add(new Parameter("generator_UNomLV", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new Parameter("generator_UBaseHV", DynawoParameterType.DOUBLE.getValue(), "69"));
        parameters.add(new Parameter("generator_UBaseLV", DynawoParameterType.DOUBLE.getValue(), "24"));
        parameters.add(new Parameter("generator_RTfPu", DynawoParameterType.DOUBLE.getValue(), "0.0"));
        parameters.add(new Parameter("generator_XTfPu", DynawoParameterType.DOUBLE.getValue(), "0.1"));
        parameters.add(new Parameter("voltageRegulator_LagEfdMax", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("voltageRegulator_LagEfdMin", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("voltageRegulator_EfdMinPu", DynawoParameterType.DOUBLE.getValue(), "-5"));
        parameters.add(new Parameter("voltageRegulator_EfdMaxPu", DynawoParameterType.DOUBLE.getValue(), "5"));
        parameters.add(new Parameter("voltageRegulator_UsRefMinPu", DynawoParameterType.DOUBLE.getValue(), "0.8"));
        parameters.add(new Parameter("voltageRegulator_UsRefMaxPu", DynawoParameterType.DOUBLE.getValue(), "1.2"));
        parameters.add(new Parameter("voltageRegulator_Gain", DynawoParameterType.DOUBLE.getValue(), "20"));
        parameters.add(new Parameter("governor_KGover", DynawoParameterType.DOUBLE.getValue(), "5"));
        parameters.add(new Parameter("governor_PMin", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("governor_PMax", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new Parameter("governor_PNom", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new Parameter("URef_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("Pm_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new Parameter("generator_P0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new Parameter("generator_Q0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new Parameter("generator_U0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new Parameter("generator_UPhase0", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "angle_pu"));
        parameterSet = new ParameterSet("4");
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Event param
        parameters = new ArrayList<>();
        parameters.add(new Parameter("event_tEvent", DynawoParameterType.DOUBLE.getValue(), "1"));
        parameters.add(new Parameter("event_disconnectOrigin", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new Parameter("event_disconnectExtremity", DynawoParameterType.BOOLEAN.getValue(), "true"));
        parameterSet = new ParameterSet("5");
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);
        Mockito.when(dynawoInputs.getParameterSets())
            .thenReturn(Collections.unmodifiableList(parameterSets));

        // Dyd file
        List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
        // Omega Ref dyd
        dynamicModels.add(new BlackBoxModel("OMEGA_REF", "DYNModelOmegaRef", "powsybl_dynawo.par", "2"));

        // Load dyd
        dynamicModels
            .add(new BlackBoxModel("_N1011____EC", "LoadAlphaBeta", "powsybl_dynawo.par", "3", "_N1011____EC"));

        // Generator dyd
        dynamicModels.add(new BlackBoxModel("_G10______SM",
            "GeneratorSynchronousFourWindingsProportionalRegulations", "powsybl_dynawo.par", "4", "_G10______SM"));

        // Event dyd
        dynamicModels
            .add(new BlackBoxModel("DISCONNECT_LINE", "EventQuadripoleDisconnection", "powsybl_dynawo.par", "5"));

        // Load connection dyd
        dynamicModels.add(new Connection("_N1011____EC", "load_terminal", "NETWORK", "_N1011____TN_ACPIN"));

        // Generator connection dyd
        dynamicModels.add(new Connection("OMEGA_REF", "omega_grp_0", "_G10______SM", "generator_omegaPu"));
        dynamicModels
            .add(new Connection("OMEGA_REF", "omegaRef_grp_0", "_G10______SM", "generator_omegaRefPu"));
        dynamicModels
            .add(new Connection("OMEGA_REF", "numcc_node_0", "NETWORK", "@_G10______SM@@NODE@_numcc"));
        dynamicModels
            .add(new Connection("OMEGA_REF", "running_grp_0", "_G10______SM", "generator_running"));
        dynamicModels.add(
            new Connection("_G10______SM", "generator_terminal", "NETWORK", "@_G10______SM@@NODE@_ACPIN"));
        dynamicModels.add(new Connection("_G10______SM", "generator_switchOffSignal1", "NETWORK",
            "@_G10______SM@@NODE@_switchOff"));

        // Event connection dyd
        dynamicModels
            .add(new Connection("DISCONNECT_LINE", "event_state1_value", "NETWORK",
                "_N1011___-N1013___-1_AC_state_value"));
        Mockito.when(dynawoInputs.getDynamicModels()).thenReturn(dynamicModels);

        // Curve file
        List<Curve> curves = new ArrayList<>();
        curves.add(new Curve("NETWORK", "_N1011____TN_Upu_value"));
        curves.add(new Curve("_G10______SM", "generator_omegaPu"));
        curves.add(new Curve("_G10______SM", "generator_PGen"));
        curves.add(new Curve("_G10______SM", "generator_QGen"));
        curves.add(new Curve("_G10______SM", "generator_UStatorPu"));
        curves.add(new Curve("_G10______SM", "voltageRegulator_UcEfdPu"));
        curves.add(new Curve("_G10______SM", "voltageRegulator_EfdPu"));
        curves.add(new Curve("_N1011____EC", "load_PPu"));
        curves.add(new Curve("_N1011____EC", "load_QPu"));
        Mockito.when(dynawoInputs.getCurves()).thenReturn(curves);
        return dynawoInputs;
    }
}

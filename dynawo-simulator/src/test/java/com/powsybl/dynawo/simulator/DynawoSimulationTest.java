/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.ComponentDefaultConfig;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoParameterType;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dsl.GroovyDslDynawoProvider;
import com.powsybl.dynawo.dyd.BlackBoxModel;
import com.powsybl.dynawo.dyd.Connection;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.job.DynawoModeler;
import com.powsybl.dynawo.job.DynawoOutputs;
import com.powsybl.dynawo.job.DynawoSimulation;
import com.powsybl.dynawo.job.DynawoSolver;
import com.powsybl.dynawo.job.LogAppender;
import com.powsybl.dynawo.par.DynawoParameter;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.dynawo.simulator.results.DynawoResults;
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
            DynawoInputProvider provider = configureProvider(network);
            DynawoResults result = tester.simulate(network, provider, platformConfig);
            LOGGER.info("metrics " + result.getMetrics().get("success"));
            assertTrue(Boolean.parseBoolean(result.getMetrics().get("success")));

            // check final voltage of bus close to the event
            int index = result.getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSeries().get(new Double(30.0)).get(index), new Double(0.931558));
        }
    }

    @Test
    public void testGroovy() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {

            PlatformConfig platformConfig = configure(fs);
            DynawoSimulationTester tester = new DynawoSimulationTester(true);
            Network network = tester.convert(platformConfig, Cim14SmallCasesCatalog.nordic32());
            DynawoInputProvider inputProvider = new GroovyDslDynawoProvider(getClass().getResourceAsStream("/nordic32/nordic32.groovy"));
            ComponentDefaultConfig defaultConfig = ComponentDefaultConfig.load(platformConfig);
            DynawoResults result = tester.simulate(network, inputProvider, platformConfig);
            LOGGER.info("metrics " + result.getMetrics().get("success"));
            assertTrue(Boolean.parseBoolean(result.getMetrics().get("success")));

            // check final voltage of bus close to the event
            int index = result.getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSeries().get(new Double(30.0)).get(index), new Double(0.931558));
        }
    }

    private PlatformConfig configure(FileSystem fs) throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fs);
        Files.createDirectory(fs.getPath("/dynawoPath"));
        Files.createDirectory(fs.getPath("/workingPath"));
        Files.createDirectories(fs.getPath("/tmp"));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("import-export-parameters-default-value");
        moduleConfig.setStringProperty("iidm.export.xml.extensions", "null");
        moduleConfig = platformConfig.createModuleConfig("computation-local");
        moduleConfig.setStringProperty("tmpDir", "/tmp");
        moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("dynawoHomeDir", "/dynawoPath");
        moduleConfig.setStringProperty("workingDir", "/workingPath");
        moduleConfig.setStringProperty("debug", "false");
        moduleConfig.setStringProperty("dynawoCptCommandName", "myEnvDynawo.sh");
        moduleConfig = platformConfig.createModuleConfig("simulation-parameters");
        moduleConfig.setStringProperty("preFaultSimulationStopInstant", "1");
        moduleConfig.setStringProperty("postFaultSimulationStopInstant", "60");
        moduleConfig.setStringProperty("faultEventInstant", "30");
        moduleConfig.setStringProperty("branchSideOneFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("branchSideTwoFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("generatorFaultShortCircuitDuration", "60");
        moduleConfig = platformConfig.createModuleConfig("componentDefaultConfig");
        moduleConfig.setStringProperty("DynawoSimulatorFactory", "com.powsybl.dynawo.simulator.DynawoSimulatorFactory");
        moduleConfig.setStringProperty("DynawoExporterFactory", "com.powsybl.dynawo.xml.DynawoXmlExporterFactory");
        return platformConfig;
    }

    private DynawoInputProvider configureProvider(Network network) {

        DynawoInputProvider dynawoProvider = Mockito.mock(DynawoInputProvider.class);

        // Job file
        DynawoSolver solver = new DynawoSolver("libdynawo_SolverIDA", "solvers.par", 2);
        DynawoModeler modeler = new DynawoModeler("outputs/compilation", "dynawoModel.xiidm", "dynawoModel.par", 1,
            "dynawoModel.dyd");
        DynawoSimulation simulation = new DynawoSimulation(0, 30, false);
        DynawoOutputs outputs = new DynawoOutputs("outputs", "dynawoModel.crv");
        outputs.add(new LogAppender("", "dynawo.log", "DEBUG"));
        outputs.add(new LogAppender("COMPILE", "dynawoCompiler.log", "DEBUG"));
        outputs.add(new LogAppender("MODELER", "dynawoModeler.log", "DEBUG"));
        DynawoJob job = new DynawoJob("Nordic 32 - Disconnect Line", solver, modeler, simulation, outputs);
        Mockito.when(dynawoProvider.getDynawoJobs(network)).thenReturn(Collections.singletonList(job));

        // Solvers file
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("order", DynawoParameterType.INT.getValue(), "2"));
        parameters.add(new DynawoParameter("initStep", DynawoParameterType.DOUBLE.getValue(), "0.000001"));
        parameters.add(new DynawoParameter("minStep", DynawoParameterType.DOUBLE.getValue(), "0.000001"));
        parameters.add(new DynawoParameter("maxStep", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new DynawoParameter("absAccuracy", DynawoParameterType.DOUBLE.getValue(), "1e-4"));
        parameters.add(new DynawoParameter("relAccuracy", DynawoParameterType.DOUBLE.getValue(), "1e-4"));
        DynawoParameterSet solverParams = new DynawoParameterSet(2);
        solverParams.addParameters(Collections.unmodifiableList(parameters));
        Mockito.when(dynawoProvider.getDynawoSolverParameterSets(network))
            .thenReturn(Collections.singletonList(solverParams));

        // Parameters file
        List<DynawoParameterSet> parameterSets = new ArrayList<>();
        // Global param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("capacitor_no_reclosing_delay", DynawoParameterType.DOUBLE.getValue(), "300"));
        parameters.add(new DynawoParameter("dangling_line_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new DynawoParameter("line_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new DynawoParameter("load_Tp", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new DynawoParameter("load_Tq", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new DynawoParameter("load_alpha", DynawoParameterType.DOUBLE.getValue(), "1"));
        parameters.add(new DynawoParameter("load_alphaLong", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("load_beta", DynawoParameterType.DOUBLE.getValue(), "2"));
        parameters.add(new DynawoParameter("load_betaLong", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("load_isControllable", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new DynawoParameter("load_isRestorative", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new DynawoParameter("load_zPMax", DynawoParameterType.DOUBLE.getValue(), "100"));
        parameters.add(new DynawoParameter("load_zQMax", DynawoParameterType.DOUBLE.getValue(), "100"));
        parameters.add(new DynawoParameter("reactance_no_reclosing_delay", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("transformer_currentLimit_maxTimeOperation", DynawoParameterType.DOUBLE.getValue(), "90"));
        parameters.add(new DynawoParameter("transformer_t1st_HT", DynawoParameterType.DOUBLE.getValue(), "60"));
        parameters.add(new DynawoParameter("transformer_t1st_THT", DynawoParameterType.DOUBLE.getValue(), "30"));
        parameters.add(new DynawoParameter("transformer_tNext_HT", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new DynawoParameter("transformer_tNext_THT", DynawoParameterType.DOUBLE.getValue(), "10"));
        parameters.add(new DynawoParameter("transformer_tolV", DynawoParameterType.DOUBLE.getValue(), "0.014999999700000001"));
        DynawoParameterSet parameterSet = new DynawoParameterSet(1);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Omega Ref param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("nbGen", DynawoParameterType.INT.getValue(), "" + network.getGeneratorCount()));
        parameters.add(new DynawoParameter("weight_gen_0", DynawoParameterType.DOUBLE.getValue(), "1211"));
        for (int i = 1; i < network.getGeneratorCount(); i++) {
            parameters.add(new DynawoParameter("weight_gen_" + i, DynawoParameterType.DOUBLE.getValue(), "1"));
        }
        parameterSet = new DynawoParameterSet(2);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Load param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("load_alpha", DynawoParameterType.DOUBLE.getValue(), "1.5"));
        parameters.add(new DynawoParameter("load_beta", DynawoParameterType.DOUBLE.getValue(), "2.5"));
        parameters.add(new DynawoParameter("load_P0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new DynawoParameter("load_Q0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new DynawoParameter("load_U0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new DynawoParameter("load_UPhase0", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "angle_pu"));
        parameterSet = new DynawoParameterSet(3);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Generator param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("generator_ExcitationPu", DynawoParameterType.INT.getValue(), "1"));
        parameters.add(new DynawoParameter("generator_DPu", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("generator_H", DynawoParameterType.DOUBLE.getValue(), "5.4000000000000004"));
        parameters.add(new DynawoParameter("generator_RaPu", DynawoParameterType.DOUBLE.getValue(), "0.0027959999999999999"));
        parameters.add(new DynawoParameter("generator_XlPu", DynawoParameterType.DOUBLE.getValue(), "0.20200000000000001"));
        parameters.add(new DynawoParameter("generator_XdPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpdPu", DynawoParameterType.DOUBLE.getValue(), "0.38400000000000001"));
        parameters.add(new DynawoParameter("generator_XppdPu", DynawoParameterType.DOUBLE.getValue(), "0.26400000000000001"));
        parameters.add(new DynawoParameter("generator_Tpd0", DynawoParameterType.DOUBLE.getValue(), "8.0939999999999994"));
        parameters.add(new DynawoParameter("generator_Tppd0", DynawoParameterType.DOUBLE.getValue(), "0.080000000000000002"));
        parameters.add(new DynawoParameter("generator_XqPu", DynawoParameterType.DOUBLE.getValue(), "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpqPu", DynawoParameterType.DOUBLE.getValue(), "0.39300000000000002"));
        parameters.add(new DynawoParameter("generator_XppqPu", DynawoParameterType.DOUBLE.getValue(), "0.26200000000000001"));
        parameters.add(new DynawoParameter("generator_Tpq0", DynawoParameterType.DOUBLE.getValue(), "1.5720000000000001"));
        parameters.add(new DynawoParameter("generator_Tppq0", DynawoParameterType.DOUBLE.getValue(), "0.084000000000000005"));
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
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMinPu", DynawoParameterType.DOUBLE.getValue(), "0.8"));
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMaxPu", DynawoParameterType.DOUBLE.getValue(), "1.2"));
        parameters.add(new DynawoParameter("voltageRegulator_Gain", DynawoParameterType.DOUBLE.getValue(), "20"));
        parameters.add(new DynawoParameter("governor_KGover", DynawoParameterType.DOUBLE.getValue(), "5"));
        parameters.add(new DynawoParameter("governor_PMin", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("governor_PMax", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new DynawoParameter("governor_PNom", DynawoParameterType.DOUBLE.getValue(), "1090"));
        parameters.add(new DynawoParameter("URef_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("Pm_ValueIn", DynawoParameterType.DOUBLE.getValue(), "0"));
        parameters.add(new DynawoParameter("generator_P0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "p_pu"));
        parameters.add(new DynawoParameter("generator_Q0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "q_pu"));
        parameters.add(new DynawoParameter("generator_U0Pu", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "v_pu"));
        parameters.add(new DynawoParameter("generator_UPhase0", DynawoParameterType.DOUBLE.getValue(), DynawoParameterType.IIDM.getValue(), "angle_pu"));
        parameterSet = new DynawoParameterSet(4);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);

        // Event param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("event_tEvent", DynawoParameterType.DOUBLE.getValue(), "1"));
        parameters.add(new DynawoParameter("event_disconnectOrigin", DynawoParameterType.BOOLEAN.getValue(), "false"));
        parameters.add(new DynawoParameter("event_disconnectExtremity", DynawoParameterType.BOOLEAN.getValue(), "true"));
        parameterSet = new DynawoParameterSet(5);
        parameterSet.addParameters(Collections.unmodifiableList(parameters));
        parameterSets.add(parameterSet);
        Mockito.when(dynawoProvider.getDynawoParameterSets(network))
            .thenReturn(Collections.unmodifiableList(parameterSets));

        // Dyd file
        List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
        // Omega Ref dyd
        dynamicModels.add(new BlackBoxModel("OMEGA_REF", "DYNModelOmegaRef", "dynawoModel.par", 2));

        // Load dyd
        dynamicModels
            .add(new BlackBoxModel("_N1011____EC", "LoadAlphaBeta", "dynawoModel.par", 3, "_N1011____EC"));

        // Generator dyd
        dynamicModels.add(new BlackBoxModel("_G10______SM",
            "GeneratorSynchronousFourWindingsProportionalRegulations", "dynawoModel.par", 4, "_G10______SM"));

        // Event dyd
        dynamicModels
            .add(new BlackBoxModel("DISCONNECT_LINE", "EventQuadripoleDisconnection", "dynawoModel.par", 5));

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
        Mockito.when(dynawoProvider.getDynawoDynamicModels(network)).thenReturn(dynamicModels);

        // Curve file
        List<DynawoCurve> curves = new ArrayList<>();
        curves.add(new DynawoCurve("NETWORK", "_N1011____TN_Upu_value"));
        curves.add(new DynawoCurve("_G10______SM", "generator_omegaPu"));
        curves.add(new DynawoCurve("_G10______SM", "generator_PGen"));
        curves.add(new DynawoCurve("_G10______SM", "generator_QGen"));
        curves.add(new DynawoCurve("_G10______SM", "generator_UStatorPu"));
        curves.add(new DynawoCurve("_G10______SM", "voltageRegulator_UcEfdPu"));
        curves.add(new DynawoCurve("_G10______SM", "voltageRegulator_EfdPu"));
        curves.add(new DynawoCurve("_N1011____EC", "load_PPu"));
        curves.add(new DynawoCurve("_N1011____EC", "load_QPu"));
        Mockito.when(dynawoProvider.getDynawoCurves(network)).thenReturn(curves);
        return dynawoProvider;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationTest.class);
}

/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.mockito.Mockito;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.DynawoCurve;
import com.powsybl.dynawo.DynawoDynamicModel;
import com.powsybl.dynawo.DynawoJob;
import com.powsybl.dynawo.DynawoModeler;
import com.powsybl.dynawo.DynawoOutputs;
import com.powsybl.dynawo.DynawoParameter;
import com.powsybl.dynawo.DynawoParameterSet;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.DynawoSimulation;
import com.powsybl.dynawo.DynawoSolver;
import com.powsybl.dynawo.simulator.DynawoSimulator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.triplestore.api.TripleStoreFactory;

public class DynawoSimulatorTester {

    private static final String IIDM = "IIDM";
    private static final String BOOLEAN = "BOOL";
    private static final String DOUBLE = "DOUBLE";
    private static final String INT = "INT";

    public DynawoSimulatorTester(PlatformConfig platformConfig) {
        this.platformConfig = platformConfig;
    }

    public ImpactAnalysisResult testGridModel(TestGridModel gm) throws Exception {
        Network network = convert(platformConfig, gm);

        // Job file
        DynawoProvider dynawoProvider = Mockito.mock(DynawoProvider.class);
        DynawoJob job = Mockito.mock(DynawoJob.class);
        Mockito.when(job.getName()).thenReturn("IEEE14 - Disconnect Line");
        Mockito.when(job.getSolver()).thenReturn(new DynawoSolver("libdynawo_SolverIDA", "solvers.par", 2));
        Mockito.when(job.getModeler()).thenReturn(
            new DynawoModeler("outputs/compilation", "dynawoModel.xiidm", "dynawoModel.par", 1, "dynawoModel.dyd"));
        Mockito.when(job.getSimulation()).thenReturn(new DynawoSimulation(0, 30, false));
        Mockito.when(job.getOutputs()).thenReturn(new DynawoOutputs("outputs", "dynawoModel.crv"));
        Mockito.when(dynawoProvider.getDynawoJob()).thenReturn(Collections.singletonList(job));

        // Solvers file
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("order", INT, "2"));
        parameters.add(new DynawoParameter("initStep", DOUBLE, "0.000001"));
        parameters.add(new DynawoParameter("minStep", DOUBLE, "0.000001"));
        parameters.add(new DynawoParameter("maxStep", DOUBLE, "10"));
        parameters.add(new DynawoParameter("absAccuracy", DOUBLE, "1e-4"));
        parameters.add(new DynawoParameter("relAccuracy", DOUBLE, "1e-4"));
        DynawoParameterSet solverParams = new DynawoParameterSet(2, Collections.unmodifiableList(parameters));
        Mockito.when(dynawoProvider.getDynawoSolverParameterSets())
            .thenReturn(Collections.singletonList(solverParams));

        // Parameters file
        List<DynawoParameterSet> parameterSets = new ArrayList<>();
        // Global param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("capacitor_no_reclosing_delay", DOUBLE, "300"));
        parameters.add(new DynawoParameter("dangling_line_currentLimit_maxTimeOperation", DOUBLE, "90"));
        parameters.add(new DynawoParameter("line_currentLimit_maxTimeOperation", DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_Tp", DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_Tq", DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_alpha", DOUBLE, "1"));
        parameters.add(new DynawoParameter("load_alphaLong", DOUBLE, "0"));
        parameters.add(new DynawoParameter("load_beta", DOUBLE, "2"));
        parameters.add(new DynawoParameter("load_betaLong", DOUBLE, "0"));
        parameters.add(new DynawoParameter("load_isControllable", BOOLEAN, "false"));
        parameters.add(new DynawoParameter("load_isRestorative", BOOLEAN, "false"));
        parameters.add(new DynawoParameter("load_zPMax", DOUBLE, "100"));
        parameters.add(new DynawoParameter("load_zQMax", DOUBLE, "100"));
        parameters.add(new DynawoParameter("reactance_no_reclosing_delay", DOUBLE, "0"));
        parameters.add(new DynawoParameter("transformer_currentLimit_maxTimeOperation", DOUBLE, "90"));
        parameters.add(new DynawoParameter("transformer_t1st_HT", DOUBLE, "60"));
        parameters.add(new DynawoParameter("transformer_t1st_THT", DOUBLE, "30"));
        parameters.add(new DynawoParameter("transformer_tNext_HT", DOUBLE, "10"));
        parameters.add(new DynawoParameter("transformer_tNext_THT", DOUBLE, "10"));
        parameters.add(new DynawoParameter("transformer_tolV", DOUBLE, "0.014999999700000001"));
        parameterSets.add(new DynawoParameterSet(1, Collections.unmodifiableList(parameters)));

        // Load param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("load_alpha", DOUBLE, "1.5"));
        parameters.add(new DynawoParameter("load_beta", DOUBLE, "2.5"));
        parameters.add(new DynawoParameter("load_P0Pu", DOUBLE, IIDM, "p_pu"));
        parameters.add(new DynawoParameter("load_Q0Pu", DOUBLE, IIDM, "q_pu"));
        parameters.add(new DynawoParameter("load_U0Pu", DOUBLE, IIDM, "v_pu"));
        parameters.add(new DynawoParameter("load_UPhase0", DOUBLE, IIDM, "angle_pu"));
        parameterSets.add(new DynawoParameterSet(2, Collections.unmodifiableList(parameters)));

        // Generator param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("generator_ExcitationPu", "INT", "1"));
        parameters.add(new DynawoParameter("generator_DPu", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("generator_H", "DOUBLE", "5.4000000000000004"));
        parameters.add(new DynawoParameter("generator_RaPu", "DOUBLE", "0.0027959999999999999"));
        parameters.add(new DynawoParameter("generator_XlPu", "DOUBLE", "0.20200000000000001"));
        parameters.add(new DynawoParameter("generator_XdPu", "DOUBLE", "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpdPu", "DOUBLE", "0.38400000000000001"));
        parameters.add(new DynawoParameter("generator_XppdPu", "DOUBLE", "0.26400000000000001"));
        parameters.add(new DynawoParameter("generator_Tpd0", "DOUBLE", "8.0939999999999994"));
        parameters.add(new DynawoParameter("generator_Tppd0", "DOUBLE", "0.080000000000000002"));
        parameters.add(new DynawoParameter("generator_XqPu", "DOUBLE", "2.2200000000000002"));
        parameters.add(new DynawoParameter("generator_XpqPu", "DOUBLE", "0.39300000000000002"));
        parameters.add(new DynawoParameter("generator_XppqPu", "DOUBLE", "0.26200000000000001"));
        parameters.add(new DynawoParameter("generator_Tpq0", "DOUBLE", "1.5720000000000001"));
        parameters.add(new DynawoParameter("generator_Tppq0", "DOUBLE", "0.084000000000000005"));
        parameters.add(new DynawoParameter("generator_UNom", "DOUBLE", "24"));
        parameters.add(new DynawoParameter("generator_SNom", "DOUBLE", "1211"));
        parameters.add(new DynawoParameter("generator_PNom", "DOUBLE", "1090"));
        parameters.add(new DynawoParameter("generator_SnTfo", "DOUBLE", "1211"));
        parameters.add(new DynawoParameter("generator_UNomHV", "DOUBLE", "69"));
        parameters.add(new DynawoParameter("generator_UNomLV", "DOUBLE", "24"));
        parameters.add(new DynawoParameter("generator_UBaseHV", "DOUBLE", "69"));
        parameters.add(new DynawoParameter("generator_UBaseLV", "DOUBLE", "24"));
        parameters.add(new DynawoParameter("generator_RTfPu", "DOUBLE", "0.0"));
        parameters.add(new DynawoParameter("generator_XTfPu", "DOUBLE", "0.1"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMax", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("voltageRegulator_LagEfdMin", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMinPu", "DOUBLE", "-5"));
        parameters.add(new DynawoParameter("voltageRegulator_EfdMaxPu", "DOUBLE", "5"));
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMinPu", "DOUBLE", "0.8"));
        parameters.add(new DynawoParameter("voltageRegulator_UsRefMaxPu", "DOUBLE", "1.2"));
        parameters.add(new DynawoParameter("voltageRegulator_Gain", "DOUBLE", "20"));
        parameters.add(new DynawoParameter("governor_KGover", "DOUBLE", "5"));
        parameters.add(new DynawoParameter("governor_PMin", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("governor_PMax", "DOUBLE", "1090"));
        parameters.add(new DynawoParameter("governor_PNom", "DOUBLE", "1090"));
        parameters.add(new DynawoParameter("URef_ValueIn", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("Pm_ValueIn", "DOUBLE", "0"));
        parameters.add(new DynawoParameter("generator_P0Pu", "DOUBLE", "IIDM", "p_pu"));
        parameters.add(new DynawoParameter("generator_Q0Pu", "DOUBLE", "IIDM", "q_pu"));
        parameters.add(new DynawoParameter("generator_U0Pu", "DOUBLE", "IIDM", "v_pu"));
        parameters.add(new DynawoParameter("generator_UPhase0", "DOUBLE", "IIDM", "angle_pu"));
        parameterSets.add(new DynawoParameterSet(3, Collections.unmodifiableList(parameters)));

        // Omega Ref param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("nbGen", INT, "1"));
        parameters.add(new DynawoParameter("weight_gen_0", DOUBLE, "1211"));
        parameterSets.add(new DynawoParameterSet(4, Collections.unmodifiableList(parameters)));

        // Event param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("event_tEvent", DOUBLE, "1"));
        parameters.add(new DynawoParameter("event_disconnectOrigin", BOOLEAN, "false"));
        parameters.add(new DynawoParameter("event_disconnectExtremity", BOOLEAN, "true"));
        parameterSets.add(new DynawoParameterSet(5, Collections.unmodifiableList(parameters)));
        Mockito.when(dynawoProvider.getDynawoParameterSets())
            .thenReturn(Collections.unmodifiableList(parameterSets));

        // Dyd file
        List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
        dynamicModels
            .add(new DynawoDynamicModel("_N1011____EC", "LoadAlphaBeta", "dynawoModel.par", 2, "_N1011____EC"));
        dynamicModels.add(new DynawoDynamicModel("_G10______SM",
            "GeneratorSynchronousFourWindingsProportionalRegulations", "dynawoModel.par", 3, "_G10______SM"));
        dynamicModels.add(new DynawoDynamicModel("OMEGA_REF", "DYNModelOmegaRef", "dynawoModel.par", 4));
        dynamicModels
            .add(new DynawoDynamicModel("DISCONNECT_LINE", "EventQuadripoleDisconnection", "dynawoModel.par", 5));
        dynamicModels.add(new DynawoDynamicModel("_N1011____EC", "load_terminal", "NETWORK", "_N1011____TN_ACPIN"));
        dynamicModels.add(new DynawoDynamicModel("OMEGA_REF", "omega_grp_0", "_G10______SM", "generator_omegaPu"));
        dynamicModels
            .add(new DynawoDynamicModel("OMEGA_REF", "omegaRef_grp_0", "_G10______SM", "generator_omegaRefPu"));
        dynamicModels
            .add(new DynawoDynamicModel("OMEGA_REF", "numcc_node_0", "NETWORK", "@_G10______SM@@NODE@_numcc"));
        dynamicModels
            .add(new DynawoDynamicModel("OMEGA_REF", "running_grp_0", "_G10______SM", "generator_running"));
        dynamicModels.add(
            new DynawoDynamicModel("_G10______SM", "generator_terminal", "NETWORK", "@_G10______SM@@NODE@_ACPIN"));
        dynamicModels.add(new DynawoDynamicModel("_G10______SM", "generator_switchOffSignal1", "NETWORK",
            "@_G10______SM@@NODE@_switchOff"));
        Mockito.when(dynawoProvider.getDynawoDynamicModels()).thenReturn(dynamicModels);

        // Dyd file
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
        Mockito.when(dynawoProvider.getDynawoCurves()).thenReturn(curves);

        DynawoSimulator simulator = new DynawoSimulator(network, platformConfig);
        simulator.simulate(dynawoProvider);

        return simulator.getResult();
    }

    private Network convert(PlatformConfig platformConfig, TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport(platformConfig);
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    private final PlatformConfig platformConfig;
}

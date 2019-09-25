/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
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
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoExporterTest extends AbstractConverterTest {

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        tmpDir = Files.createDirectory(fileSystem.getPath("/tmp"));
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        network = importNetwork(platformConfig, catalog.nordic32());
        network.setCaseDate(DateTime.parse("2019-09-23T11:06:12.313+02:00"));
        dynawoProvider = configureProvider(network);
    }

    @Test
    public void export() throws IOException {
        DynawoExporter exporter = new DynawoExporter(network, dynawoProvider);
        exporter.export(tmpDir, platformConfig);
        Files.walk(tmpDir).forEach(file -> {
            if (Files.isRegularFile(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    compareXml(getClass().getResourceAsStream("/nordic32/" + file.getFileName()), is);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
    }

    private Network importNetwork(PlatformConfig platformConfig, TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport(platformConfig);
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    private DynawoProvider configureProvider(Network network) {
        // Job file
        DynawoProvider dynawoProvider = Mockito.mock(DynawoProvider.class);
        DynawoSolver solver = new DynawoSolver("libdynawo_SolverIDA", "solvers.par", 2);
        DynawoModeler modeler = new DynawoModeler("outputs/compilation", "dynawoModel.xiidm", "dynawoModel.par", 1,
            "dynawoModel.dyd");
        DynawoSimulation simulation = new DynawoSimulation(0, 30, false);
        DynawoOutputs outputs = new DynawoOutputs("outputs", "dynawoModel.crv");
        DynawoJob job = new DynawoJob("Nordic 32 - Disconnect Line", solver, modeler, simulation, outputs);
        Mockito.when(dynawoProvider.getDynawoJob(network)).thenReturn(Collections.singletonList(job));

        // Solvers file
        List<DynawoParameter> parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("order", DynawoInput.INT, "2"));
        parameters.add(new DynawoParameter("initStep", DynawoInput.DOUBLE, "0.000001"));
        parameters.add(new DynawoParameter("minStep", DynawoInput.DOUBLE, "0.000001"));
        parameters.add(new DynawoParameter("maxStep", DynawoInput.DOUBLE, "10"));
        parameters.add(new DynawoParameter("absAccuracy", DynawoInput.DOUBLE, "1e-4"));
        parameters.add(new DynawoParameter("relAccuracy", DynawoInput.DOUBLE, "1e-4"));
        DynawoParameterSet solverParams = new DynawoParameterSet(2, Collections.unmodifiableList(parameters));
        Mockito.when(dynawoProvider.getDynawoSolverParameterSets(network))
            .thenReturn(Collections.singletonList(solverParams));

        // Parameters file
        List<DynawoParameterSet> parameterSets = new ArrayList<>();
        // Global param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("capacitor_no_reclosing_delay", DynawoInput.DOUBLE, "300"));
        parameters.add(new DynawoParameter("dangling_line_currentLimit_maxTimeOperation", DynawoInput.DOUBLE, "90"));
        parameters.add(new DynawoParameter("line_currentLimit_maxTimeOperation", DynawoInput.DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_Tp", DynawoInput.DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_Tq", DynawoInput.DOUBLE, "90"));
        parameters.add(new DynawoParameter("load_alpha", DynawoInput.DOUBLE, "1"));
        parameters.add(new DynawoParameter("load_alphaLong", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("load_beta", DynawoInput.DOUBLE, "2"));
        parameters.add(new DynawoParameter("load_betaLong", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("load_isControllable", DynawoInput.BOOLEAN, "false"));
        parameters.add(new DynawoParameter("load_isRestorative", DynawoInput.BOOLEAN, "false"));
        parameters.add(new DynawoParameter("load_zPMax", DynawoInput.DOUBLE, "100"));
        parameters.add(new DynawoParameter("load_zQMax", DynawoInput.DOUBLE, "100"));
        parameters.add(new DynawoParameter("reactance_no_reclosing_delay", DynawoInput.DOUBLE, "0"));
        parameters.add(new DynawoParameter("transformer_currentLimit_maxTimeOperation", DynawoInput.DOUBLE, "90"));
        parameters.add(new DynawoParameter("transformer_t1st_HT", DynawoInput.DOUBLE, "60"));
        parameters.add(new DynawoParameter("transformer_t1st_THT", DynawoInput.DOUBLE, "30"));
        parameters.add(new DynawoParameter("transformer_tNext_HT", DynawoInput.DOUBLE, "10"));
        parameters.add(new DynawoParameter("transformer_tNext_THT", DynawoInput.DOUBLE, "10"));
        parameters.add(new DynawoParameter("transformer_tolV", DynawoInput.DOUBLE, "0.014999999700000001"));
        parameterSets.add(new DynawoParameterSet(1, Collections.unmodifiableList(parameters)));

        // Omega Ref param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("nbGen", DynawoInput.INT, "" + network.getGeneratorCount()));
        parameters.add(new DynawoParameter("weight_gen_0", DynawoInput.DOUBLE, "1211"));
        for (int i = 1; i < network.getGeneratorCount(); i++) {
            parameters.add(new DynawoParameter("weight_gen_" + i, DynawoInput.DOUBLE, "1"));
        }
        parameterSets.add(new DynawoParameterSet(2, Collections.unmodifiableList(parameters)));

        // Load param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("load_alpha", DynawoInput.DOUBLE, "1.5"));
        parameters.add(new DynawoParameter("load_beta", DynawoInput.DOUBLE, "2.5"));
        parameters.add(new DynawoParameter("load_P0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "p_pu"));
        parameters.add(new DynawoParameter("load_Q0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "q_pu"));
        parameters.add(new DynawoParameter("load_U0Pu", DynawoInput.DOUBLE, DynawoInput.IIDM, "v_pu"));
        parameters.add(new DynawoParameter("load_UPhase0", DynawoInput.DOUBLE, DynawoInput.IIDM, "angle_pu"));
        parameterSets.add(new DynawoParameterSet(3, Collections.unmodifiableList(parameters)));

        // Generator param
        parameters = new ArrayList<>();
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
        parameterSets.add(new DynawoParameterSet(4, Collections.unmodifiableList(parameters)));

        // Event param
        parameters = new ArrayList<>();
        parameters.add(new DynawoParameter("event_tEvent", DynawoInput.DOUBLE, "1"));
        parameters.add(new DynawoParameter("event_disconnectOrigin", DynawoInput.BOOLEAN, "false"));
        parameters.add(new DynawoParameter("event_disconnectExtremity", DynawoInput.BOOLEAN, "true"));
        parameterSets.add(new DynawoParameterSet(5, Collections.unmodifiableList(parameters)));
        Mockito.when(dynawoProvider.getDynawoParameterSets(network))
            .thenReturn(Collections.unmodifiableList(parameterSets));

        // Dyd file
        List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
        // Omega Ref dyd
        dynamicModels.add(new DynawoDynamicModel("OMEGA_REF", "DYNModelOmegaRef", "dynawoModel.par", 2));

        // Load dyd
        dynamicModels
            .add(new DynawoDynamicModel("_N1011____EC", "LoadAlphaBeta", "dynawoModel.par", 3, "_N1011____EC"));

        // Generator dyd
        dynamicModels.add(new DynawoDynamicModel("_G10______SM",
            "GeneratorSynchronousFourWindingsProportionalRegulations", "dynawoModel.par", 4, "_G10______SM"));

        // Event dyd
        dynamicModels
            .add(new DynawoDynamicModel("DISCONNECT_LINE", "EventQuadripoleDisconnection", "dynawoModel.par", 5));

        // Load connection dyd
        dynamicModels.add(new DynawoDynamicModel("_N1011____EC", "load_terminal", "NETWORK", "_N1011____TN_ACPIN"));

        // Generator connection dyd
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

        // Event connection dyd
        dynamicModels
            .add(new DynawoDynamicModel("DISCONNECT_LINE", "event_state1_value", "NETWORK",
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

    private Network network;
    private DynawoProvider dynawoProvider;
    private final Cim14SmallCasesCatalog catalog = new Cim14SmallCasesCatalog();
    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoExporterTest.class);
}

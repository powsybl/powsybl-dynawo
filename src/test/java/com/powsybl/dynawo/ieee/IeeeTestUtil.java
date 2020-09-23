/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.DynamicSimulation;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyCurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.DynawoParameters;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.DynawoProviderTest.EventModelsSupplierMock;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class IeeeTestUtil {

    protected FileSystem fileSystem;
    protected Path tmpDir;
    protected Network network;
    protected DynamicSimulationParameters parameters;

    private DynamicModelsSupplier dynamicModelsSupplier;
    private EventModelsSupplier eventModelsSupplier;
    private CurvesSupplier curvesSupplier;

    public void setup(String parametersFile, String networkParametersFile, String solverParametersFile, String networkFile, String curvesFile,
        String dynamicModels, String parametersJson) throws IOException {

        // The parameter files are copied into the PlatformConfig filesystem,
        // that filesystem is the one that DynawoContext and ParametersXml will use to read the parameters
        fileSystem = PlatformConfig.defaultConfig().getConfigDir().getFileSystem();
        tmpDir = Files.createDirectory(fileSystem.getPath("tmp"));

        // Copy parameter files
        Files.copy(getClass().getResourceAsStream(parametersFile), fileSystem.getPath("/work/ieee-models.par"));
        Files.copy(getClass().getResourceAsStream(networkParametersFile), fileSystem.getPath("/work/ieee-network.par"));
        Files.copy(getClass().getResourceAsStream(solverParametersFile), fileSystem.getPath("/work/ieee-solvers.par"));

        // Load network
        Files.copy(getClass().getResourceAsStream(networkFile), fileSystem.getPath("/network.iidm"));
        network = Importers.loadNetwork(fileSystem.getPath("/network.iidm"));

        // Copy groovy files
        Files.copy(getClass().getResourceAsStream(curvesFile), fileSystem.getPath("/curves.groovy"));
        Files.copy(getClass().getResourceAsStream(dynamicModels), fileSystem.getPath("/dynamicModels.groovy"));

        // Load DynamicSimulationParameters
        Files.copy(getClass().getResourceAsStream(parametersJson), fileSystem.getPath("/dynawoParameters.json"));
        parameters = JsonDynamicSimulationParameters.read(fileSystem.getPath("/dynawoParameters.json"));

        // Prepare suppliers
        List<CurveGroovyExtension> curveGroovyExtensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        curvesSupplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves.groovy"), curveGroovyExtensions);
        List<DynamicModelGroovyExtension> dynamicModelGroovyExtensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoProvider.NAME);
        dynamicModelsSupplier = new GroovyDynamicModelsSupplier(fileSystem.getPath("/dynamicModels.groovy"), dynamicModelGroovyExtensions);
        eventModelsSupplier = new EventModelsSupplierMock();
    }

    protected DynawoParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoParameters dynawoParameters = parameters.getExtension(DynawoParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoParameters.load();
        }
        return dynawoParameters;
    }

    public DynamicSimulationResult runSimulation(LocalCommandExecutor commandExecutor) throws Exception {
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynawoProvider.NAME, dynawoSimulation.getName());
        assertEquals("1.2.0", dynawoSimulation.getVersion());
        return dynawoSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier,
            curvesSupplier, network.getVariantManager().getWorkingVariantId(),
            computationManager, parameters);
    }

}

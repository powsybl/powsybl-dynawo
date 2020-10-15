/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee;

import org.junit.After;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.io.FileUtil;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.DynawoParameters;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.DynawoProviderTest.DynamicModelsSupplierMock;
import com.powsybl.dynawo.DynawoProviderTest.EventModelsSupplierMock;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertEquals;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractIeeeTest {

    protected FileSystem fileSystem;
    protected Path workingDir;
    protected Network network;
    protected DynamicSimulationParameters parameters;

    private DynamicModelsSupplier dynamicModelsSupplier;
    private EventModelsSupplier eventModelsSupplier;
    private CurvesSupplier curvesSupplier;

    @After
    public void tearDown() throws IOException {
        FileUtil.removeDir(workingDir);
    }

    public abstract String getWorkingDirName();

    protected void setup(String parametersFile, String networkParametersFile, String solverParametersFile, String networkFile,
        String dynamicModelsFile, String eventModelsFile, String curvesFile, String parametersJson) throws IOException {

        // The parameter files are copied into the PlatformConfig filesystem,
        // that filesystem is the one that DynawoContext and ParametersXml will use to read the parameters
        fileSystem = PlatformConfig.defaultConfig().getConfigDir().getFileSystem();
        workingDir = Files.createDirectory(fileSystem.getPath(getWorkingDirName()));

        // Copy parameter files
        Files.copy(getClass().getResourceAsStream(parametersFile), workingDir.resolve("ieee-models.par"));
        Files.copy(getClass().getResourceAsStream(networkParametersFile), workingDir.resolve("ieee-network.par"));
        Files.copy(getClass().getResourceAsStream(solverParametersFile), workingDir.resolve("ieee-solvers.par"));

        // Load network
        Files.copy(getClass().getResourceAsStream(networkFile), workingDir.resolve("network.iidm"));
        network = Importers.loadNetwork(workingDir.resolve("network.iidm"));

        // Dynamic models
        if (dynamicModelsFile != null) {
            Files.copy(getClass().getResourceAsStream(dynamicModelsFile), workingDir.resolve("dynamicModels.groovy"));
            List<DynamicModelGroovyExtension> dynamicModelGroovyExtensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoProvider.NAME);
            dynamicModelsSupplier = new GroovyDynamicModelsSupplier(workingDir.resolve("dynamicModels.groovy"), dynamicModelGroovyExtensions);
        } else {
            dynamicModelsSupplier = new DynamicModelsSupplierMock();
        }

        // Event models
        if (eventModelsFile != null) {
            Files.copy(getClass().getResourceAsStream(eventModelsFile), workingDir.resolve("eventModels.groovy"));
            List<EventModelGroovyExtension> eventModelGroovyExtensions = GroovyExtension.find(EventModelGroovyExtension.class, DynawoProvider.NAME);
            eventModelsSupplier = new GroovyEventModelsSupplier(workingDir.resolve("eventModels.groovy"), eventModelGroovyExtensions);
        } else {
            eventModelsSupplier = new EventModelsSupplierMock();
        }

        // Curves
        if (curvesFile != null) {
            Files.copy(getClass().getResourceAsStream(curvesFile), workingDir.resolve("curves.groovy"));
            List<CurveGroovyExtension> curveGroovyExtensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
            curvesSupplier = new GroovyCurvesSupplier(workingDir.resolve("curves.groovy"), curveGroovyExtensions);
        } else {
            curvesSupplier = CurvesSupplier.empty();
        }

        // Parameters
        Files.copy(getClass().getResourceAsStream(parametersJson), workingDir.resolve("dynawoParameters.json"));
        parameters = JsonDynamicSimulationParameters.read(workingDir.resolve("dynawoParameters.json"));
    }

    protected DynawoParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoParameters dynawoParameters = parameters.getExtension(DynawoParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoParameters.load();
        }
        return dynawoParameters;
    }

    public DynamicSimulationResult runSimulation(LocalCommandExecutor commandExecutor) throws Exception {
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(workingDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynawoProvider.NAME, dynawoSimulation.getName());
        assertEquals("1.2.0", dynawoSimulation.getVersion());
        return dynawoSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier,
            curvesSupplier, network.getVariantManager().getWorkingVariantId(),
            computationManager, parameters);
    }

}

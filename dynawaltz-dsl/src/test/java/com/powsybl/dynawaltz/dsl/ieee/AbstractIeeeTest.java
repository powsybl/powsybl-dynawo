/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.io.FileUtil;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.powsybl.commons.report.ReportNode.NO_OP;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public abstract class AbstractIeeeTest {

    protected FileSystem fileSystem;
    protected Path workingDir;
    protected Network network;
    protected DynamicSimulationParameters parameters;

    private DynamicModelsSupplier dynamicModelsSupplier;
    private EventModelsSupplier eventModelsSupplier;
    private CurvesSupplier curvesSupplier;

    @AfterEach
    void tearDown() throws IOException {
        FileUtil.removeDir(workingDir);
    }

    public abstract String getWorkingDirName();

    protected void setup(String parametersFile, String networkParametersFile, String networkParametersId, String solverParametersFile, String solverParametersId, String networkFile,
                         String dynamicModelsFile, String eventModelsFile, String curvesFile, int startTime, int stopTime) throws IOException {

        // The parameter files are copied into the PlatformConfig filesystem,
        // that filesystem is the one that DynaWaltzContext and ParametersXml will use to read the parameters
        fileSystem = PlatformConfig.defaultConfig().getConfigDir().map(Path::getFileSystem).orElseThrow(AssertionError::new);
        workingDir = Files.createDirectory(fileSystem.getPath(getWorkingDirName()));

        // Load network
        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream(networkFile)), workingDir.resolve("network.iidm"));
        network = Network.read(workingDir.resolve("network.iidm"));

        // Dynamic models
        if (dynamicModelsFile != null) {
            Files.copy(Objects.requireNonNull(getClass().getResourceAsStream(dynamicModelsFile)), workingDir.resolve("dynamicModels.groovy"));
            List<DynamicModelGroovyExtension> dynamicModelGroovyExtensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);
            dynamicModelsSupplier = new GroovyDynamicModelsSupplier(workingDir.resolve("dynamicModels.groovy"), dynamicModelGroovyExtensions);
        } else {
            dynamicModelsSupplier = (n, NO_OP) -> Collections.emptyList();
        }

        // Event models
        if (eventModelsFile != null) {
            Files.copy(Objects.requireNonNull(getClass().getResourceAsStream(eventModelsFile)), workingDir.resolve("eventModels.groovy"));
            List<EventModelGroovyExtension> eventModelGroovyExtensions = GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME);
            eventModelsSupplier = new GroovyEventModelsSupplier(workingDir.resolve("eventModels.groovy"), eventModelGroovyExtensions);
        } else {
            eventModelsSupplier = EventModelsSupplier.empty();
        }

        // Curves
        if (curvesFile != null) {
            Files.copy(Objects.requireNonNull(getClass().getResourceAsStream(curvesFile)), workingDir.resolve("curves.groovy"));
            List<CurveGroovyExtension> curveGroovyExtensions = GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME);
            curvesSupplier = new GroovyCurvesSupplier(workingDir.resolve("curves.groovy"), curveGroovyExtensions);
        } else {
            curvesSupplier = CurvesSupplier.empty();
        }

        parameters = new DynamicSimulationParameters()
                .setStartTime(startTime)
                .setStopTime(stopTime);
        DynaWaltzParameters dynaWaltzParameters = new DynaWaltzParameters();
        parameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
        dynaWaltzParameters.setModelsParameters(ParametersXml.load(getClass().getResourceAsStream(parametersFile)))
                .setNetworkParameters(ParametersXml.load(getClass().getResourceAsStream(networkParametersFile), networkParametersId))
                .setSolverParameters(ParametersXml.load(getClass().getResourceAsStream(solverParametersFile), solverParametersId))
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters();
    }

    protected DynaWaltzParameters getDynaWaltzSimulationParameters(DynamicSimulationParameters parameters) {
        DynaWaltzParameters dynaWaltzParameters = parameters.getExtension(DynaWaltzParameters.class);
        if (dynaWaltzParameters == null) {
            dynaWaltzParameters = DynaWaltzParameters.load();
        }
        return dynaWaltzParameters;
    }

    public DynamicSimulationResult runSimulation(LocalCommandExecutor commandExecutor) throws Exception {
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(workingDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        return dynawoSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier,
            curvesSupplier, network.getVariantManager().getWorkingVariantId(),
            computationManager, parameters, NO_OP);
    }

}

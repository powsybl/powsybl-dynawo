/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;
import com.powsybl.dynawaltz.parameters.Parameter;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.xml.ParametersXml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class DynaWaltzParametersTest extends AbstractSerDeTest {

    public static final String USER_HOME = "/home/user/";

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    private void copyFile(String name, String parametersFile) throws IOException {
        Path path = platformConfig.getConfigDir()
                .map(cd -> cd.resolve(fileSystem.getPath(parametersFile)))
                .orElse(fileSystem.getPath(parametersFile));
        Objects.requireNonNull(getClass().getResourceAsStream(name))
                .transferTo(Files.newOutputStream(path));
    }

    @Test
    void checkParameters() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = true;
        boolean useModelSimplifiers = true;
        double precision = 1e-8;
        ExportMode timelinExportMode = ExportMode.XML;
        DynaWaltzParameters.LogLevel logLevel = DynaWaltzParameters.LogLevel.WARN;
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, useModelSimplifiers, precision, timelinExportMode, logLevel);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);

        checkModelParameters(parameters);

        assertEquals(networkParametersId, parameters.getNetworkParameters().getId());
        Map<String, Parameter> networkParameters = parameters.getNetworkParameters().getParameters();
        Parameter loadTp = networkParameters.get("load_Tp");
        assertEquals("90", loadTp.value());
        assertEquals("load_Tp", loadTp.name());
        assertEquals(ParameterType.DOUBLE, loadTp.type());
        Parameter loadControllable = networkParameters.get("load_isControllable");
        assertEquals("false", loadControllable.value());
        assertEquals("load_isControllable", loadControllable.name());
        assertEquals(ParameterType.BOOL, loadControllable.type());

        Map<String, Parameter> solverParameters = parameters.getSolverParameters().getParameters();
        assertEquals(solverParametersId, parameters.getSolverParameters().getId());
        assertEquals(solverType, parameters.getSolverType());
        Parameter order = solverParameters.get("order");
        assertEquals("1", order.value());
        assertEquals("order", order.name());
        assertEquals(ParameterType.INT, order.type());
        Parameter absAccuracy = solverParameters.get("absAccuracy");
        assertEquals("1e-4", absAccuracy.value());
        assertEquals("absAccuracy", absAccuracy.name());
        assertEquals(ParameterType.DOUBLE, absAccuracy.type());

        assertEquals(mergeLoads, parameters.isMergeLoads());
        assertEquals(useModelSimplifiers, parameters.isUseModelSimplifiers());
        assertEquals(precision, parameters.getPrecision());
        assertEquals(timelinExportMode, parameters.getTimelineExportMode());
        assertEquals(logLevel, parameters.getLogLevelFilter());
    }

    @Test
    void checkDumpFileParameters() throws IOException {
        String folderProperty = USER_HOME + "dumpFiles";
        String fileProperty = "dumpFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
        DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();

        assertTrue(dumpFileParameters.exportDumpFile());
        assertTrue(dumpFileParameters.useDumpFile());
        assertEquals(folderProperty, dumpFileParameters.dumpFileFolder().toString());
        assertEquals(fileProperty, dumpFileParameters.dumpFile());
    }

    @Test
    void roundTripParametersSerializing() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = false;
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, false, 1e-7, ExportMode.TXT, DynaWaltzParameters.LogLevel.INFO);

        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(3600);
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynawoParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
                JsonDynamicSimulationParameters::read, "/DynaWaltzParameters.json");
    }

    private void initPlatformConfig(String networkParametersId, SolverType solverType, String solverParametersId, boolean mergeLoads, boolean useModelSimplifiers, double precision, ExportMode timelineExportMode, DynaWaltzParameters.LogLevel logLevel) throws IOException {
        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("mergeLoads", String.valueOf(mergeLoads));
        moduleConfig.setStringProperty("network.parametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);
        moduleConfig.setStringProperty("useModelSimplifiers", String.valueOf(useModelSimplifiers));
        moduleConfig.setStringProperty("precision", Double.toString(precision));
        moduleConfig.setStringProperty("timeline.exportMode", String.valueOf(timelineExportMode));
        moduleConfig.setStringProperty("log.levelFilter", logLevel.toString());

        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", parametersFile);
        copyFile("/parametersSet/network.par", networkParametersFile);
        copyFile("/parametersSet/solvers.par", solverParametersFile);
    }

    private void initDumpFilePlatformConfig(String folderProperty, String fileProperty) throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        String folderName = USER_HOME + "dumpFiles";
        Files.createDirectories(fileSystem.getPath(folderName));
        String fileName = "dumpFile.dmp";
        Files.createFile(fileSystem.getPath(folderName, fileName));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        moduleConfig.setStringProperty("network.parametersFile", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        moduleConfig.setStringProperty("dump.export", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.useAsInput", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.exportFolder", folderProperty);
        moduleConfig.setStringProperty("dump.fileName", fileProperty);

    }

    @Test
    void checkDefaultParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
        checkModelParameters(parameters);

        assertEquals(DynaWaltzParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetworkParameters().getId());
        assertTrue(parameters.getNetworkParameters().getParameters().isEmpty());
        assertTrue(parameters.getNetworkParameters().getReferences().isEmpty());

        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_TYPE, parameters.getSolverType());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolverParameters().getId());
        assertEquals("1", parameters.getSolverParameters().getId());

        assertEquals(DynaWaltzParameters.DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
        assertEquals(DynaWaltzParameters.DEFAULT_USE_MODEL_SIMPLIFIERS, parameters.isUseModelSimplifiers());
        assertEquals(DynaWaltzParameters.DEFAULT_TIMELINE_EXPORT_MODE, parameters.getTimelineExportMode());
    }

    @Test
    void checkDefaultDumpParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
        DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();
        assertEquals(DumpFileParameters.DEFAULT_EXPORT_DUMP, dumpFileParameters.exportDumpFile());
        assertEquals(DumpFileParameters.DEFAULT_USE_DUMP, dumpFileParameters.useDumpFile());
        assertNull(dumpFileParameters.dumpFileFolder());
        assertEquals(DumpFileParameters.DEFAULT_DUMP_NAME, dumpFileParameters.dumpFile());
    }

    @Test
    void checkException() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solversMissingDefault.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        PowsyblException e1 = assertThrows(PowsyblException.class, () -> DynaWaltzParameters.load(platformConfig, fileSystem));
        assertEquals("Could not find parameters set with id='1' in file '/work/inmemory/solvers.par'", e1.getMessage());

        try (InputStream is = getClass().getResourceAsStream("/parametersSet/solvers.par")) {
            PowsyblException e2 = assertThrows(PowsyblException.class, () -> ParametersXml.load(is, "2"));
            assertEquals("Could not find parameters set with id='2' in given input stream", e2.getMessage());
        }
    }

    @Test
    void dumpFilesFolderNotFound() throws IOException {
        String folderProperty = USER_HOME + "wrongFolder";
        String fileProperty = "dumpFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        PowsyblException e = assertThrows(PowsyblException.class, () -> DynaWaltzParameters.load(platformConfig, fileSystem));
        assertEquals("Folder /home/user/wrongFolder set in 'dumpFileFolder' property cannot be found", e.getMessage());
    }

    @Test
    void dumpFileNotFound() throws IOException {
        String folderProperty = USER_HOME + "dumpFiles";
        String fileProperty = "wrongFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        PowsyblException e = assertThrows(PowsyblException.class, () -> DynaWaltzParameters.load(platformConfig, fileSystem));
        assertEquals("File wrongFile.dmp set in 'dumpFile' property cannot be found", e.getMessage());
    }

    private static void checkModelParameters(DynaWaltzParameters dynaWaltzParameters) {
        Parameter booleanParameter = dynaWaltzParameters.getModelParameters("test").getParameters().get("boolean");
        assertEquals("true", booleanParameter.value());
        assertEquals("boolean", booleanParameter.name());
        assertEquals(ParameterType.BOOL, booleanParameter.type());
        Parameter stringParameter = dynaWaltzParameters.getModelParameters("test").getParameters().get("string");
        assertEquals("aString", stringParameter.value());
        assertEquals("string", stringParameter.name());
        assertEquals(ParameterType.STRING, stringParameter.type());
    }
}

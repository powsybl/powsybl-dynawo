/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationParameters.ExportMode;
import com.powsybl.dynawo.DynawoSimulationParameters.LogLevel;
import com.powsybl.dynawo.DynawoSimulationParameters.SolverType;
import com.powsybl.dynawo.DynawoSimulationParameters.SpecificLog;
import com.powsybl.dynawo.parameters.Parameter;
import com.powsybl.dynawo.parameters.ParameterType;
import com.powsybl.dynawo.xml.ParametersXml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class DynawoParametersTest extends AbstractSerDeTest {

    public static final String USER_HOME = "/home/user/";

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    @Override
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
        LogLevel logLevel = LogLevel.WARN;
        Set<SpecificLog> specificLogs = EnumSet.of(SpecificLog.MODELER, SpecificLog.EQUATIONS);
        String criteriaFileName = "criteria.crt";

        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, useModelSimplifiers, precision, timelinExportMode, logLevel, specificLogs, criteriaFileName);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);

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
        assertEquals(specificLogs, parameters.getSpecificLogs());
        assertEquals(criteriaFileName, parameters.getCriteriaFileName());
        assertEquals(fileSystem.getPath(USER_HOME + criteriaFileName), parameters.getCriteriaFilePath());
    }

    @Test
    void checkDumpFileParameters() throws IOException {
        String folderProperty = USER_HOME + "dumpFiles";
        String fileProperty = "dumpFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);
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
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, false, 1e-7, ExportMode.TXT, LogLevel.INFO, Set.of(SpecificLog.PARAMETERS, SpecificLog.VARIABLES), null);

        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(3600);
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynawoSimulationParameters.class, dynawoParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
                JsonDynamicSimulationParameters::read, "/DynawoSimulationParameters.json");
    }

    private void initPlatformConfig(String networkParametersId, SolverType solverType, String solverParametersId,
                                    boolean mergeLoads, boolean useModelSimplifiers, double precision, ExportMode timelineExportMode,
                                    LogLevel logLevel, Set<SpecificLog> specificLogs, String criteriaFileName) throws IOException {
        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";
        String criteriaFile = criteriaFileName != null ? USER_HOME + criteriaFileName : null;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-simulation-default-parameters");
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
        moduleConfig.setStringListProperty("log.specificLogs", specificLogs.stream().map(SpecificLog::toString).toList());
        moduleConfig.setStringProperty("criteria.file", criteriaFile);

        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", parametersFile);
        copyFile("/parametersSet/network.par", networkParametersFile);
        copyFile("/parametersSet/solvers.par", solverParametersFile);
        if (criteriaFile != null) {
            copyFile("/criteria.crt", criteriaFile);
        }
    }

    private void initDumpFilePlatformConfig(String folderProperty, String fileProperty) throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynawoSimulationParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        String folderName = USER_HOME + "dumpFiles";
        Files.createDirectories(fileSystem.getPath(folderName));
        String fileName = "dumpFile.dmp";
        Files.createFile(fileSystem.getPath(folderName, fileName));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-simulation-default-parameters");
        moduleConfig.setStringProperty("parametersFile", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        moduleConfig.setStringProperty("network.parametersFile", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        moduleConfig.setStringProperty("dump.export", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.useAsInput", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.exportFolder", folderProperty);
        moduleConfig.setStringProperty("dump.fileName", fileProperty);

    }

    @Test
    void checkDefaultParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynawoSimulationParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);
        checkModelParameters(parameters);

        assertEquals(DynawoSimulationParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetworkParameters().getId());
        assertTrue(parameters.getNetworkParameters().getParameters().isEmpty());
        assertTrue(parameters.getNetworkParameters().getReferences().isEmpty());

        assertEquals(DynawoSimulationParameters.DEFAULT_SOLVER_TYPE, parameters.getSolverType());
        assertEquals(DynawoSimulationParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolverParameters().getId());
        assertEquals("1", parameters.getSolverParameters().getId());

        assertEquals(DynawoSimulationParameters.DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
        assertEquals(DynawoSimulationParameters.DEFAULT_USE_MODEL_SIMPLIFIERS, parameters.isUseModelSimplifiers());
        assertEquals(DynawoSimulationParameters.DEFAULT_TIMELINE_EXPORT_MODE, parameters.getTimelineExportMode());
        assertFalse(parameters.hasCriteriaFile());
    }

    @Test
    void checkDefaultDumpParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynawoSimulationParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);
        DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();
        assertEquals(DumpFileParameters.DEFAULT_EXPORT_DUMP, dumpFileParameters.exportDumpFile());
        assertEquals(DumpFileParameters.DEFAULT_USE_DUMP, dumpFileParameters.useDumpFile());
        assertNull(dumpFileParameters.dumpFileFolder());
        assertEquals(DumpFileParameters.DEFAULT_DUMP_NAME, dumpFileParameters.dumpFile());
    }

    @Test
    void checkException() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solversMissingDefault.par", DynawoSimulationParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        PowsyblException e1 = assertThrows(PowsyblException.class, () -> DynawoSimulationParameters.load(platformConfig, fileSystem));
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
        PowsyblException e = assertThrows(PowsyblException.class, () -> DynawoSimulationParameters.load(platformConfig, fileSystem));
        assertEquals("Folder /home/user/wrongFolder set in 'dumpFileFolder' property cannot be found", e.getMessage());
    }

    @Test
    void dumpFileNotFound() throws IOException {
        String folderProperty = USER_HOME + "dumpFiles";
        String fileProperty = "wrongFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        PowsyblException e = assertThrows(PowsyblException.class, () -> DynawoSimulationParameters.load(platformConfig, fileSystem));
        assertEquals("File wrongFile.dmp set in 'dumpFile' property cannot be found", e.getMessage());
    }

    private static void checkModelParameters(DynawoSimulationParameters dynawoSimulationParameters) {
        Parameter booleanParameter = dynawoSimulationParameters.getModelParameters("test").getParameters().get("boolean");
        assertEquals("true", booleanParameter.value());
        assertEquals("boolean", booleanParameter.name());
        assertEquals(ParameterType.BOOL, booleanParameter.type());
        Parameter stringParameter = dynawoSimulationParameters.getModelParameters("test").getParameters().get("string");
        assertEquals("aString", stringParameter.value());
        assertEquals("string", stringParameter.name());
        assertEquals(ParameterType.STRING, stringParameter.type());
    }
}

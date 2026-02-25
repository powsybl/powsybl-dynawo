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
import com.powsybl.commons.extensions.Extension;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.commons.ExportMode;
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

import static com.powsybl.dynawo.DynawoSimulationParameters.*;
import static org.assertj.core.api.Assertions.assertThat;
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
        List<String> modelSimplifiers = List.of("Filter1", "Filter2");
        double precision = 1e-8;
        ExportMode timelinExportMode = ExportMode.XML;
        LogLevel logLevel = LogLevel.WARN;
        Set<SpecificLog> specificLogs = EnumSet.of(SpecificLog.MODELER, SpecificLog.EQUATIONS);
        String criteriaFileName = "criteria.crt";
        String additionalModelsFileName = "additionalModels.json";

        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, modelSimplifiers,
                precision, timelinExportMode, logLevel, specificLogs, criteriaFileName, additionalModelsFileName);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);

        checkModelParameters(parameters);
        checkNetworkParameters(parameters, networkParametersId);
        checkSolverParameters(parameters, solverParametersId, solverType);

        assertEquals(mergeLoads, parameters.isMergeLoads());
        assertThat(parameters.getModelSimplifiers()).containsAll(modelSimplifiers);
        assertEquals(precision, parameters.getPrecision());
        assertEquals(timelinExportMode, parameters.getTimelineExportMode());
        assertEquals(logLevel, parameters.getLogLevelFilter());
        assertEquals(specificLogs, parameters.getSpecificLogs());
        assertThat(parameters.getCriteriaFileName()).hasValue(criteriaFileName);
        assertThat(parameters.getCriteriaFilePath()).hasValue(fileSystem.getPath(USER_HOME + criteriaFileName));
        assertThat(parameters.getAdditionalModelsPath()).hasValue(fileSystem.getPath(USER_HOME + additionalModelsFileName));
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
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, List.of("Filter"),
                1e-7, ExportMode.TXT, LogLevel.INFO, Set.of(SpecificLog.PARAMETERS, SpecificLog.VARIABLES),
                null, null);

        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(3600);
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynawoSimulationParameters.class, dynawoParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
                JsonDynamicSimulationParameters::read, "/DynawoSimulationParameters.json");
    }

    private void initPlatformConfig(String networkParametersId, SolverType solverType, String solverParametersId,
                                    boolean mergeLoads, List<String> modelSimplifiers, double precision, ExportMode timelineExportMode,
                                    LogLevel logLevel, Set<SpecificLog> specificLogs, String criteriaFileName,
                                    String additionalModelsFileName) throws IOException {
        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";
        String criteriaFile = criteriaFileName != null ? USER_HOME + criteriaFileName : null;
        String additionalModelsFile = additionalModelsFileName != null ? USER_HOME + additionalModelsFileName : null;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("mergeLoads", String.valueOf(mergeLoads));
        moduleConfig.setStringProperty("network.parametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);
        moduleConfig.setStringListProperty("modelSimplifiers", modelSimplifiers);
        moduleConfig.setStringProperty("precision", Double.toString(precision));
        moduleConfig.setStringProperty("timeline.exportMode", String.valueOf(timelineExportMode));
        moduleConfig.setStringProperty("log.levelFilter", logLevel.toString());
        moduleConfig.setStringListProperty("log.specificLogs", specificLogs.stream().map(SpecificLog::toString).toList());
        moduleConfig.setStringProperty("criteria.file", criteriaFile);
        moduleConfig.setStringProperty("additionalModelsFile", additionalModelsFile);

        createFiles(parametersFile, networkParametersFile, solverParametersFile, criteriaFile, additionalModelsFile);
    }

    private void initDumpFilePlatformConfig(String folderProperty, String fileProperty) throws IOException {
        String folderName = USER_HOME + "dumpFiles";
        String fileName = "dumpFile.dmp";
        MapModuleConfig moduleConfig = (MapModuleConfig) platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                .orElseGet(() -> platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS));
        moduleConfig.setStringProperty("dump.export", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.useAsInput", Boolean.toString(true));
        moduleConfig.setStringProperty("dump.exportFolder", folderProperty);
        moduleConfig.setStringProperty("dump.fileName", fileProperty);

        createDumpFiles(folderName, fileName);
    }

    @Test
    void checkDefaultParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("parametersFile", "/work/inmemory/models.par");
        moduleConfig.setStringProperty("network.parametersFile", "/work/inmemory/network.par");
        moduleConfig.setStringProperty("solver.parametersFile", "/work/inmemory/solvers.par");

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig, fileSystem);
        checkModelParameters(parameters);

        assertEquals(DEFAULT_NETWORK_PAR_ID, parameters.getNetworkParameters().getId());
        assertTrue(parameters.getNetworkParameters().getParameters().isEmpty());
        assertTrue(parameters.getNetworkParameters().getReferences().isEmpty());

        assertEquals(DEFAULT_SOLVER_TYPE, parameters.getSolverType());
        assertEquals(DEFAULT_SOLVER_PAR_ID, parameters.getSolverParameters().getId());
        assertEquals("SIM", parameters.getSolverParameters().getId());
        assertEquals(DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
        assertTrue(parameters.getModelSimplifiers().isEmpty());
        assertEquals(DEFAULT_TIMELINE_EXPORT_MODE, parameters.getTimelineExportMode());
        assertTrue(parameters.getCriteriaFilePath().isEmpty());
        assertTrue(parameters.getAdditionalModelsPath().isEmpty());
    }

    @Test
    void checkDefaultDumpParameters() {
        DynawoSimulationParameters parameters = load(platformConfig, fileSystem);
        DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();
        assertEquals(DumpFileParameters.DEFAULT_EXPORT_DUMP, dumpFileParameters.exportDumpFile());
        assertEquals(DumpFileParameters.DEFAULT_USE_DUMP, dumpFileParameters.useDumpFile());
        assertEquals(DumpFileParameters.DEFAULT_DUMP_FOLDER, dumpFileParameters.dumpFileFolder());
        assertEquals(DumpFileParameters.DEFAULT_DUMP_NAME, dumpFileParameters.dumpFile());
    }

    @Test
    void checkException() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/solversMissingDefault.par", DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("solver.parametersFile", "/work/inmemory/solvers.par");

        PowsyblException e1 = assertThrows(PowsyblException.class, () -> load(platformConfig, fileSystem));
        assertEquals("Could not find parameters set with id='SIM' in file '/work/inmemory/solvers.par'", e1.getMessage());

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
        PowsyblException e = assertThrows(PowsyblException.class, () -> load(platformConfig, fileSystem));
        assertEquals("Folder /home/user/wrongFolder set in 'dumpFileFolder' property cannot be found", e.getMessage());
    }

    @Test
    void dumpFileNotFound() throws IOException {
        String folderProperty = USER_HOME + "dumpFiles";
        String fileProperty = "wrongFile.dmp";
        initDumpFilePlatformConfig(folderProperty, fileProperty);
        PowsyblException e = assertThrows(PowsyblException.class, () -> load(platformConfig, fileSystem));
        assertEquals("File wrongFile.dmp set in 'dumpFile' property cannot be found", e.getMessage());
    }

    @Test
    void testGetParametersMap() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = true;
        List<String> modelSimplifiers = List.of("Substitution", "Filter");
        double precision = 1e-8;
        ExportMode timelinExportMode = ExportMode.XML;
        LogLevel logLevel = LogLevel.WARN;
        Set<SpecificLog> specificLogs = EnumSet.of(SpecificLog.MODELER, SpecificLog.EQUATIONS);
        String criteriaFileName = "criteria.crt";
        String dumpFolder = USER_HOME + "dumpFiles";
        String dumpFile = "dumpFile.dmp";
        String additionalModelsFileName = "additionalModels.json";

        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads, modelSimplifiers, precision, timelinExportMode, logLevel, specificLogs, criteriaFileName, additionalModelsFileName);
        initDumpFilePlatformConfig(dumpFolder, dumpFile);
        Map<String, String> expectedProperties = Map.ofEntries(
                Map.entry("modelParameters",
                        "{test=test,{boolean=Parameter[name=boolean, type=BOOL, value=true], string=Parameter[name=string, type=STRING, value=aString]},{}}"),
                Map.entry("networkParameters",
                        "networkParametersId,{load_Tp=Parameter[name=load_Tp, type=DOUBLE, value=90], load_isControllable=Parameter[name=load_isControllable, type=BOOL, value=false]},{}"),
                Map.entry("solverParameters",
                        "solverParametersId,{order=Parameter[name=order, type=INT, value=1], absAccuracy=Parameter[name=absAccuracy, type=DOUBLE, value=1e-4]},{}"),
                Map.entry("solver.type", "IDA"),
                Map.entry("mergeLoads", "true"),
                Map.entry("modelSimplifiers", "Substitution,Filter"),
                Map.entry("precision", "1.0E-8"),
                Map.entry("timeline.exportMode", "XML"),
                Map.entry("log.levelFilter", "WARN"),
                Map.entry("log.specificLogs", "MODELER,EQUATIONS"),
                Map.entry("criteria.file", "/home/user/criteria.crt"),
                Map.entry("additionalModelsFile", "/home/user/additionalModels.json"),
                Map.entry("dump.export", "true"),
                Map.entry("dump.exportFolder", "/home/user/dumpFiles"),
                Map.entry("dump.useAsInput", "true"),
                Map.entry("dump.fileName", "dumpFile.dmp"));

        Map<String, String> properties = DynawoSimulationParameters.load(platformConfig, fileSystem)
                .createMapFromParameters();
        assertThat(properties).containsExactlyInAnyOrderEntriesOf(expectedProperties);
    }

    @Test
    void loadMapDynawoParameters() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = true;
        List<String> modelSimplifiers = List.of("Substitution", "Filter");
        double precision = 1e-8;
        ExportMode timelinExportMode = ExportMode.XML;
        LogLevel logLevel = LogLevel.WARN;
        Set<SpecificLog> specificLogs = EnumSet.of(SpecificLog.MODELER, SpecificLog.EQUATIONS);
        String criteriaFileName = "criteria.crt";
        String additionalModelsFileName = "additionalModels.json";
        String dumpFolder = USER_HOME + "dumpFiles";
        String dumpFile = "dumpFile.dmp";
        boolean useDumpFile = true;
        boolean exportDumpFile = true;

        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";
        String criteriaFile = USER_HOME + criteriaFileName;
        String additionalModelsFile = USER_HOME + "additionalModels.json";

        Map<String, String> properties = new HashMap<>();
        properties.put("parametersFile", parametersFile);
        properties.put("network.parametersFile", networkParametersFile);
        properties.put("network.parametersId", networkParametersId);
        properties.put("solver.parametersFile", solverParametersFile);
        properties.put("solver.parametersId", solverParametersId);
        properties.put("solver.type", solverType.toString());
        properties.put("mergeLoads", Boolean.toString(mergeLoads));
        properties.put("modelSimplifiers", "Substitution, Filter");
        properties.put("precision", Double.toString(precision));
        properties.put("timeline.exportMode", timelinExportMode.toString());
        properties.put("log.levelFilter", logLevel.toString());
        properties.put("log.specificLogs", "MODELER, EQUATIONS");
        properties.put("criteria.file", criteriaFile);
        properties.put("additionalModelsFile", additionalModelsFile);
        properties.put("dump.export", Boolean.toString(exportDumpFile));
        properties.put("dump.exportFolder", dumpFolder);
        properties.put("dump.useAsInput", Boolean.toString(useDumpFile));
        properties.put("dump.fileName", dumpFile);

        createFiles(parametersFile, networkParametersFile, solverParametersFile, criteriaFile, additionalModelsFile);
        createDumpFiles(dumpFolder, dumpFile);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(properties, fileSystem);
        checkModelParameters(parameters);
        checkNetworkParameters(parameters, networkParametersId);
        checkSolverParameters(parameters, solverParametersId, solverType);
        assertEquals(mergeLoads, parameters.isMergeLoads());
        assertThat(parameters.getModelSimplifiers()).containsAll(modelSimplifiers);
        assertEquals(precision, parameters.getPrecision());
        assertEquals(timelinExportMode, parameters.getTimelineExportMode());
        assertEquals(logLevel, parameters.getLogLevelFilter());
        assertThat(parameters.getSpecificLogs()).containsExactlyInAnyOrderElementsOf(specificLogs);
        assertThat(parameters.getCriteriaFileName()).hasValue(criteriaFileName);
        assertThat(parameters.getCriteriaFilePath()).hasValue(fileSystem.getPath(USER_HOME + criteriaFileName));
        assertThat(parameters.getAdditionalModelsPath()).hasValue(fileSystem.getPath(USER_HOME + additionalModelsFileName));
        DumpFileParameters dumpParameters = parameters.getDumpFileParameters();
        assertEquals(exportDumpFile, dumpParameters.exportDumpFile());
        assertEquals(useDumpFile, dumpParameters.useDumpFile());
        assertEquals(dumpFolder, dumpParameters.dumpFileFolder().toString());
        assertEquals(dumpFile, dumpParameters.dumpFile());
    }

    @Test
    void loadAndUploadFromExtendable() {
        DynawoSimulationProvider provider = new DynawoSimulationProvider();
        Optional<Extension<DynamicSimulationParameters>> specificParameters = provider.loadSpecificParameters(Map.of("log.specificLogs", "EQUATIONS"));
        assertThat(specificParameters).isPresent();
        DynawoSimulationParameters parameters = (DynawoSimulationParameters) specificParameters.get();
        assertThat(parameters.getSpecificLogs()).containsExactly(SpecificLog.EQUATIONS);
        parameters.addSpecificLog(SpecificLog.PARAMETERS);
        provider.updateSpecificParameters(parameters, Map.of("mergeLoads", "True"));
        assertThat(parameters.getSpecificLogs()).containsExactly(SpecificLog.PARAMETERS, SpecificLog.EQUATIONS);
        assertTrue(parameters.isMergeLoads());
    }

    @Test
    void partialUpdate() throws IOException {
        initPlatformConfig("networkParametersId", SolverType.SIM, "solverParametersId", DEFAULT_MERGE_LOADS, List.of(),
                1e-7, DEFAULT_TIMELINE_EXPORT_MODE, DEFAULT_LOG_LEVEL_FILTER, EnumSet.noneOf(SpecificLog.class),
                null, null);
        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynawoSimulationParameters.class, dynawoParameters);
        assertFalse(dynawoParameters.isMergeLoads());
        assertEquals(1e-7, dynawoParameters.getPrecision());
        assertEquals(SolverType.SIM, dynawoParameters.getSolverType());
        assertEquals("solverParametersId", dynawoParameters.getSolverParameters().getId());

        JsonDynamicSimulationParameters.update(dynamicSimulationParameters, getClass().getResourceAsStream("/partial_dynawo_parameters_update.json"));
        assertEquals(1, dynamicSimulationParameters.getStartTime());
        DynawoSimulationParameters parameters = dynamicSimulationParameters.getExtension(DynawoSimulationParameters.class);
        assertFalse(parameters.isMergeLoads());
        //set to 1e-7 in platform config
        assertEquals(1e-7, parameters.getPrecision());
        assertEquals(SolverType.IDA, parameters.getSolverType());
        assertEquals("ida", parameters.getSolverParameters().getId());
    }

    private void createFiles(String parametersFile, String networkParametersFile, String solverParametersFile, String criteriaFile, String additionalModelsFile) throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", parametersFile);
        copyFile("/parametersSet/network.par", networkParametersFile);
        copyFile("/parametersSet/solvers.par", solverParametersFile);
        if (criteriaFile != null) {
            copyFile("/criteria.crt", criteriaFile);
        }
        if (additionalModelsFile != null) {
            copyFile("/additionalModels.json", additionalModelsFile);
        }
    }

    private void createDumpFiles(String folderName, String fileName) throws IOException {
        Files.createDirectories(fileSystem.getPath(folderName));
        Files.createFile(fileSystem.getPath(folderName, fileName));
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

    private static void checkNetworkParameters(DynawoSimulationParameters parameters, String networkParametersId) {
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
    }

    private static void checkSolverParameters(DynawoSimulationParameters parameters, String solverParametersId, SolverType solverType) {
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
    }
}

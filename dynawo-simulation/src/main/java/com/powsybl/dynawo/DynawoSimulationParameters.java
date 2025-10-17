/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.parameters.ParameterType;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;

import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.commons.ParametersUtils.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
@JsonIgnoreProperties(value = { "criteriaFileName" })
public class DynawoSimulationParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final String MODULE_SPECIFIC_PARAMETERS = "dynawo-simulation-default-parameters";

    public static final String DEFAULT_INPUT_PARAMETERS_FILE = "models.par";
    public static final String DEFAULT_INPUT_NETWORK_PARAMETERS_FILE = "network.par";
    public static final String DEFAULT_INPUT_SOLVER_PARAMETERS_FILE = "solvers.par";
    public static final String DEFAULT_INPUT_LOCAL_INIT_PARAMETERS_FILE = "init.par";
    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final String DEFAULT_NETWORK_PAR_ID = "1";
    public static final String DEFAULT_SOLVER_PAR_ID = "1";
    public static final String DEFAULT_LOCAL_INIT_PAR_ID = "1";
    public static final boolean DEFAULT_MERGE_LOADS = false;
    public static final boolean DEFAULT_USE_MODEL_SIMPLIFIERS = false;
    public static final double DEFAULT_PRECISION = 1e-6;
    public static final ExportMode DEFAULT_TIMELINE_EXPORT_MODE = ExportMode.TXT;
    public static final LogLevel DEFAULT_LOG_LEVEL_FILTER = LogLevel.INFO;

    private static final String PARAMETERS_FILE = "parametersFile";
    private static final String NETWORK_PARAMETERS_FILE = "network.parametersFile";
    private static final String NETWORK_PARAMETERS_ID = "network.parametersId";
    private static final String SOLVER_PARAMETERS_FILE = "solver.parametersFile";
    private static final String SOLVER_PARAMETERS_ID = "solver.parametersId";
    private static final String LOCAL_INIT_PARAMETERS_FILE = "localInit.parametersFile";
    private static final String LOCAL_INIT_PARAMETERS_ID = "localInit.parametersId";
    private static final String SOLVER_TYPE = "solver.type";
    private static final String MERGE_LOADS = "mergeLoads";
    private static final String USE_MODEL_SIMPLIFIERS = "useModelSimplifiers";
    private static final String PRECISION_PROPERTY_NAME = "precision";
    private static final String TIMELINE_EXPORT_MODE = "timeline.exportMode";
    private static final String LOG_LEVEL_FILTER = "log.levelFilter";
    private static final String LOG_SPECIFIC_LOGS = "log.specificLogs";
    private static final String CRITERIA_FILE = "criteria.file";
    private static final String ADDITIONAL_MODELS_FILE = "additionalModelsFile";

    /**
     * Information about the solver to use in the simulation
     */
    public enum SolverType {
        /**
         * the simplified solver
         */
        SIM,
        /**
         * the IDA solver
         */
        IDA
    }

    public enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public enum SpecificLog {

        NETWORK("network"),
        MODELER("modeler"),
        PARAMETERS("param"),
        VARIABLES("variables"),
        EQUATIONS("equations");

        private static final String DEFAULT_FILE_EXTENSION = ExportMode.TXT.getFileExtension();

        private final String fileName;

        SpecificLog(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName + DEFAULT_FILE_EXTENSION;
        }
    }

    private Map<String, ParametersSet> modelsParameters = new LinkedHashMap<>();
    private ParametersSet networkParameters;
    private ParametersSet solverParameters;
    private ParametersSet localInitParameters;
    private SolverType solverType = DEFAULT_SOLVER_TYPE;
    private boolean mergeLoads = DEFAULT_MERGE_LOADS;
    private boolean useModelSimplifiers = DEFAULT_USE_MODEL_SIMPLIFIERS;
    private DumpFileParameters dumpFileParameters = DumpFileParameters.createDefaultDumpFileParameters();
    private double precision = DEFAULT_PRECISION;
    private ExportMode timelineExportMode = DEFAULT_TIMELINE_EXPORT_MODE;
    private LogLevel logLevelFilter = DEFAULT_LOG_LEVEL_FILTER;
    private EnumSet<SpecificLog> specificLogs = EnumSet.noneOf(SpecificLog.class);
    private Path criteriaFilePath = null;
    private Path additionalModelsPath = null;

    public static final List<Parameter> SPECIFIC_PARAMETERS = Stream.concat(Stream.of(
            new Parameter(PARAMETERS_FILE, ParameterType.STRING, "Main parameters file path", DEFAULT_INPUT_PARAMETERS_FILE),
            new Parameter(NETWORK_PARAMETERS_FILE, ParameterType.STRING, "Network parameters file path", DEFAULT_INPUT_NETWORK_PARAMETERS_FILE),
            new Parameter(NETWORK_PARAMETERS_ID, ParameterType.STRING, "Network parameters set id", DEFAULT_NETWORK_PAR_ID),
            new Parameter(SOLVER_PARAMETERS_FILE, ParameterType.STRING, "Solver parameters file path", DEFAULT_INPUT_SOLVER_PARAMETERS_FILE),
            new Parameter(SOLVER_PARAMETERS_ID, ParameterType.STRING, "Solver parameters set id", DEFAULT_SOLVER_PAR_ID),
            new Parameter(LOCAL_INIT_PARAMETERS_FILE, ParameterType.STRING, "Used in some specific cases in order to replace the solver parameters at initialization", DEFAULT_INPUT_LOCAL_INIT_PARAMETERS_FILE),
            new Parameter(LOCAL_INIT_PARAMETERS_ID, ParameterType.STRING, "Local init parameters set id", DEFAULT_LOCAL_INIT_PAR_ID),
            new Parameter(SOLVER_TYPE, ParameterType.STRING, "Solver used in the simulation", DEFAULT_SOLVER_TYPE.toString(), getEnumPossibleValues(SolverType.class)),
            new Parameter(MERGE_LOADS, ParameterType.BOOLEAN, "Merge loads connected to same bus", DEFAULT_MERGE_LOADS),
            new Parameter(USE_MODEL_SIMPLIFIERS, ParameterType.BOOLEAN, "Simplifiers used before macro connection computation", DEFAULT_USE_MODEL_SIMPLIFIERS),
            new Parameter(PRECISION_PROPERTY_NAME, ParameterType.DOUBLE, "Simulation step precision", DEFAULT_PRECISION),
            new Parameter(TIMELINE_EXPORT_MODE, ParameterType.STRING, "Timeline export file extension", DEFAULT_TIMELINE_EXPORT_MODE.toString(), getEnumPossibleValues(ExportMode.class)),
            new Parameter(LOG_LEVEL_FILTER, ParameterType.STRING, "Dynawo log level", DEFAULT_LOG_LEVEL_FILTER.toString(), getEnumPossibleValues(LogLevel.class)),
            new Parameter(LOG_SPECIFIC_LOGS, ParameterType.STRING, "List specific logs returned", null, getEnumPossibleValues(SpecificLog.class)),
            new Parameter(CRITERIA_FILE, ParameterType.STRING, "Simulation criteria file path", null),
            new Parameter(ADDITIONAL_MODELS_FILE, ParameterType.STRING, "Additional models file path", null)),
            DumpFileParameters.SPECIFIC_PARAMETERS.stream()).toList();

    /**
     * Loads parameters from the default platform configuration.
     */
    public static DynawoSimulationParameters load() {
        return load(PlatformConfig.defaultConfig());
    }

    /**
     * Load parameters from a provided platform configuration.
     */
    public static DynawoSimulationParameters load(PlatformConfig platformConfig) {
        return load(platformConfig, FileSystems.getDefault());
    }

    public static DynawoSimulationParameters load(PlatformConfig platformConfig, FileSystem fileSystem) {
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        config.ifPresent(c -> {
            c.getOptionalStringProperty(PARAMETERS_FILE).ifPresent(f -> {
                Path path = resolveFilePath(f, platformConfig, fileSystem);
                if (Files.exists(path)) {
                    parameters.setModelsParameters(ParametersXml.load(path));
                }
            });
            c.getOptionalStringProperty(NETWORK_PARAMETERS_FILE).ifPresent(f -> {
                Path path = resolveFilePath(f, platformConfig, fileSystem);
                if (Files.exists(path)) {
                    parameters.setNetworkParameters(ParametersXml.load(path,
                            c.getOptionalStringProperty(NETWORK_PARAMETERS_ID).orElse(DEFAULT_NETWORK_PAR_ID)));
                }
            });
            c.getOptionalStringProperty(SOLVER_PARAMETERS_FILE).ifPresent(f -> {
                Path path = resolveFilePath(f, platformConfig, fileSystem);
                if (Files.exists(path)) {
                    parameters.setSolverParameters(ParametersXml.load(path,
                            c.getOptionalStringProperty(SOLVER_PARAMETERS_ID).orElse(DEFAULT_SOLVER_PAR_ID)));
                }
            });
            c.getOptionalStringProperty(LOCAL_INIT_PARAMETERS_FILE).ifPresent(f -> {
                Path path = resolveFilePath(f, platformConfig, fileSystem);
                if (Files.exists(path)) {
                    parameters.setLocalInitParameters(ParametersXml.load(path,
                            c.getOptionalStringProperty(LOCAL_INIT_PARAMETERS_ID).orElse(DEFAULT_LOCAL_INIT_PAR_ID)));
                }
            });
            parameters.setDumpFileParameters(DumpFileParameters.createDumpFileParametersFromConfig(c, f -> resolveFilePath(f, platformConfig, fileSystem)));
            c.getOptionalEnumProperty(SOLVER_TYPE, SolverType.class).ifPresent(parameters::setSolverType);
            c.getOptionalBooleanProperty(MERGE_LOADS).ifPresent(parameters::setMergeLoads);
            c.getOptionalBooleanProperty(USE_MODEL_SIMPLIFIERS).ifPresent(parameters::setUseModelSimplifiers);
            c.getOptionalDoubleProperty(PRECISION_PROPERTY_NAME).ifPresent(parameters::setPrecision);
            c.getOptionalEnumProperty(TIMELINE_EXPORT_MODE, ExportMode.class).ifPresent(parameters::setTimelineExportMode);
            c.getOptionalEnumProperty(LOG_LEVEL_FILTER, LogLevel.class).ifPresent(parameters::setLogLevelFilter);
            c.getOptionalEnumSetProperty(LOG_SPECIFIC_LOGS, SpecificLog.class).ifPresent(parameters::setSpecificLogs);
            c.getOptionalStringProperty(CRITERIA_FILE).ifPresent(cf -> parameters.setCriteriaFilePath(resolveFilePath(cf, platformConfig, fileSystem)));
            c.getOptionalStringProperty(ADDITIONAL_MODELS_FILE).ifPresent(am -> parameters.setAdditionalModelsPath(resolveFilePath(am, platformConfig, fileSystem)));
        });
        return parameters;
    }

    private static Path resolveFilePath(String fileName, PlatformConfig platformConfig, FileSystem fileSystem) {
        return platformConfig.getConfigDir().map(configDir -> configDir.resolve(fileName)).orElse(fileSystem.getPath(fileName));
    }

    public static DynawoSimulationParameters load(Map<String, String> properties) {
        return load(properties, FileSystems.getDefault());
    }

    public static DynawoSimulationParameters load(Map<String, String> properties, FileSystem fileSystem) {
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        parameters.update(properties, fileSystem);
        return parameters;
    }

    public static DynawoSimulationParameters load(DynamicSimulationParameters parameters) {
        DynawoSimulationParameters dynawoSimulationParameters = parameters.getExtension(DynawoSimulationParameters.class);
        if (dynawoSimulationParameters == null) {
            dynawoSimulationParameters = DynawoSimulationParameters.load();
        }
        return dynawoSimulationParameters;
    }

    @Override
    public String getName() {
        return "DynawoSimulationParameters";
    }

    public void update(Map<String, String> properties) {
        update(properties, FileSystems.getDefault());
    }

    public void update(Map<String, String> properties, FileSystem fileSystem) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(PARAMETERS_FILE)).ifPresent(prop -> {
            Path path = fileSystem.getPath(prop);
            if (Files.exists(path)) {
                setModelsParameters(ParametersXml.load(path));
            }
        });
        Optional.ofNullable(properties.get(NETWORK_PARAMETERS_FILE)).ifPresent(prop -> {
            Path path = fileSystem.getPath(prop);
            if (Files.exists(path)) {
                setNetworkParameters(ParametersXml.load(path,
                        Optional.ofNullable(properties.get(NETWORK_PARAMETERS_ID)).orElse(DEFAULT_NETWORK_PAR_ID)));
            }
        });
        Optional.ofNullable(properties.get(SOLVER_PARAMETERS_FILE)).ifPresent(prop -> {
            Path path = fileSystem.getPath(prop);
            if (Files.exists(path)) {
                setSolverParameters(ParametersXml.load(path,
                        Optional.ofNullable(properties.get(SOLVER_PARAMETERS_ID)).orElse(DEFAULT_SOLVER_PAR_ID)));
            }
        });
        Optional.ofNullable(properties.get(SOLVER_TYPE)).ifPresent(prop -> setSolverType(SolverType.valueOf(prop)));
        Optional.ofNullable(properties.get(MERGE_LOADS)).ifPresent(prop -> setMergeLoads(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(USE_MODEL_SIMPLIFIERS)).ifPresent(prop -> setUseModelSimplifiers(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(PRECISION_PROPERTY_NAME)).ifPresent(prop -> setPrecision(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(TIMELINE_EXPORT_MODE)).ifPresent(prop -> setTimelineExportMode(ExportMode.valueOf(prop)));
        Optional.ofNullable(properties.get(LOG_LEVEL_FILTER)).ifPresent(prop -> setLogLevelFilter(LogLevel.valueOf(prop)));
        Optional.ofNullable(properties.get(LOG_SPECIFIC_LOGS)).ifPresent(prop ->
                setSpecificLogs(Stream.of(prop.split(PROPERTY_LIST_DELIMITER)).map(o -> SpecificLog.valueOf(o.trim())).collect(Collectors.toSet())));
        Optional.ofNullable(properties.get(CRITERIA_FILE)).ifPresent(prop -> setCriteriaFilePath(prop, fileSystem));
        Optional.ofNullable(properties.get(ADDITIONAL_MODELS_FILE)).ifPresent(prop -> setAdditionalModelsPath(prop, fileSystem));
        dumpFileParameters = DumpFileParameters.updateDumpFileParametersFromPropertiesMap(properties, dumpFileParameters, fileSystem::getPath);
    }

    public Map<String, String> createMapFromParameters() {
        Map<String, String> properties = new HashMap<>();
        addNotNullEntry("modelParameters", modelsParameters, properties::put);
        addNotNullEntry("networkParameters", networkParameters, properties::put);
        addNotNullEntry("solverParameters", solverParameters, properties::put);
        addNotNullEntry("localInitParameters", localInitParameters, properties::put);
        addNotNullEntry(SOLVER_TYPE, solverType, properties::put);
        addNotNullEntry(MERGE_LOADS, mergeLoads, properties::put);
        addNotNullEntry(USE_MODEL_SIMPLIFIERS, useModelSimplifiers, properties::put);
        addNotNullEntry(PRECISION_PROPERTY_NAME, precision, properties::put);
        addNotNullEntry(TIMELINE_EXPORT_MODE, timelineExportMode, properties::put);
        addNotNullEntry(LOG_LEVEL_FILTER, logLevelFilter, properties::put);
        if (!specificLogs.isEmpty()) {
            properties.put(LOG_SPECIFIC_LOGS, String.join(PROPERTY_LIST_DELIMITER, specificLogs.stream().map(SpecificLog::name).toList()));
        }
        addNotNullEntry(CRITERIA_FILE, criteriaFilePath, properties::put);
        addNotNullEntry(ADDITIONAL_MODELS_FILE, additionalModelsPath, properties::put);
        dumpFileParameters.addParametersToMap((k, v) -> addNotNullEntry(k, v, properties::put));
        return properties;
    }

    public void addModelParameters(ParametersSet parameterSet) {
        modelsParameters.put(parameterSet.getId(), parameterSet);
    }

    public ParametersSet getModelParameters(String parameterSetId) {
        ParametersSet parametersSet = modelsParameters.get(parameterSetId);
        if (parametersSet == null) {
            throw new PowsyblException("Model parameter set " + parameterSetId + " not found");
        }
        return parametersSet;
    }

    @JsonGetter("modelsParameters")
    public Collection<ParametersSet> getModelParameters() {
        return modelsParameters.values();
    }

    @JsonSetter("modelsParameters")
    public DynawoSimulationParameters setModelsParameters(Collection<ParametersSet> parametersSets) {
        modelsParameters = new LinkedHashMap<>();
        parametersSets.forEach(parametersSet -> modelsParameters.put(parametersSet.getId(), parametersSet));
        return this;
    }

    public DynawoSimulationParameters setModelsParameters(InputStream inputStream) {
        setModelsParameters(ParametersXml.load(inputStream));
        return this;
    }

    public DynawoSimulationParameters setNetworkParameters(ParametersSet networkParameters) {
        this.networkParameters = Objects.requireNonNull(networkParameters);
        return this;
    }

    public DynawoSimulationParameters setNetworkParameters(InputStream inputStream, String parameterSetId) {
        this.networkParameters = ParametersXml.load(inputStream, parameterSetId);
        return this;
    }

    public ParametersSet getNetworkParameters() {
        return networkParameters;
    }

    public DynawoSimulationParameters setSolverParameters(ParametersSet solverParameters) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        return this;
    }

    public DynawoSimulationParameters setSolverParameters(InputStream inputStream, String parameterSetId) {
        this.solverParameters = ParametersXml.load(inputStream, parameterSetId);
        return this;
    }

    public ParametersSet getSolverParameters() {
        return solverParameters;
    }

    public DynawoSimulationParameters setLocalInitParameters(ParametersSet localInitParameters) {
        this.localInitParameters = localInitParameters;
        return this;
    }

    public ParametersSet getLocalInitParameters() {
        return Objects.requireNonNullElseGet(localInitParameters, () -> new ParametersSet(DEFAULT_LOCAL_INIT_PAR_ID));
    }

    public DynawoSimulationParameters setSolverType(SolverType solverType) {
        this.solverType = solverType;
        return this;
    }

    public SolverType getSolverType() {
        return solverType;
    }

    public boolean isMergeLoads() {
        return mergeLoads;
    }

    public DynawoSimulationParameters setMergeLoads(boolean mergeLoads) {
        this.mergeLoads = mergeLoads;
        return this;
    }

    public boolean isUseModelSimplifiers() {
        return useModelSimplifiers;
    }

    public DynawoSimulationParameters setUseModelSimplifiers(boolean useModelSimplifiers) {
        this.useModelSimplifiers = useModelSimplifiers;
        return this;
    }

    public DumpFileParameters getDumpFileParameters() {
        return dumpFileParameters;
    }

    public DynawoSimulationParameters setDumpFileParameters(DumpFileParameters dumpFileParameters) {
        this.dumpFileParameters = dumpFileParameters;
        return this;
    }

    public double getPrecision() {
        return precision;
    }

    public DynawoSimulationParameters setPrecision(double precision) {
        this.precision = precision;
        return this;
    }

    public ExportMode getTimelineExportMode() {
        return timelineExportMode;
    }

    public DynawoSimulationParameters setTimelineExportMode(ExportMode timelineExportMode) {
        this.timelineExportMode = timelineExportMode;
        return this;
    }

    public LogLevel getLogLevelFilter() {
        return logLevelFilter;
    }

    public DynawoSimulationParameters setLogLevelFilter(LogLevel logLevelFilter) {
        this.logLevelFilter = logLevelFilter;
        return this;
    }

    public Set<SpecificLog> getSpecificLogs() {
        return specificLogs;
    }

    public DynawoSimulationParameters setSpecificLogs(Set<SpecificLog> specificLogs) {
        if (specificLogs.isEmpty()) {
            this.specificLogs = EnumSet.noneOf(SpecificLog.class);
        } else {
            this.specificLogs = EnumSet.copyOf(specificLogs);
        }
        return this;
    }

    public DynawoSimulationParameters addSpecificLog(SpecificLog specificLog) {
        specificLogs.add(specificLog);
        return this;
    }

    public Optional<Path> getCriteriaFilePath() {
        return Optional.ofNullable(criteriaFilePath);
    }

    public Optional<String> getCriteriaFileName() {
        return getCriteriaFilePath().map(c -> c.getFileName().toString());
    }

    public DynawoSimulationParameters setCriteriaFilePath(Path criteriaFilePath) {
        this.criteriaFilePath = criteriaFilePath;
        return this;
    }

    private void setCriteriaFilePath(String criteriaPathName, FileSystem fileSystem) {
        Path criteriaPath = criteriaPathName != null ? fileSystem.getPath(criteriaPathName) : null;
        if (criteriaPath == null || !Files.exists(criteriaPath)) {
            throw new PowsyblException("File " + criteriaPathName + " set in 'criteria.file' property cannot be found");
        }
        setCriteriaFilePath(criteriaPath);
    }

    public Optional<Path> getAdditionalModelsPath() {
        return Optional.ofNullable(additionalModelsPath);
    }

    public DynawoSimulationParameters setAdditionalModelsPath(Path additionalModelsPath) {
        this.additionalModelsPath = additionalModelsPath;
        return this;
    }

    private void setAdditionalModelsPath(String additionalModelsPathName, FileSystem fileSystem) {
        Path path = additionalModelsPathName != null ? fileSystem.getPath(additionalModelsPathName) : null;
        if (path == null || !Files.exists(path)) {
            throw new PowsyblException("File " + additionalModelsPathName + " set in 'additionalModelsFile' property cannot be found");
        }
        setAdditionalModelsPath(path);
    }
}

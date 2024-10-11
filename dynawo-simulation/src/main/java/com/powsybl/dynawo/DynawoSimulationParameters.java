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
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
@JsonIgnoreProperties(value = { "criteriaFileName" })
public class DynawoSimulationParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final String DEFAULT_NETWORK_PAR_ID = "1";
    public static final String DEFAULT_SOLVER_PAR_ID = "1";
    public static final boolean DEFAULT_MERGE_LOADS = false;
    public static final String DEFAULT_INPUT_PARAMETERS_FILE = "models.par";
    public static final String DEFAULT_INPUT_NETWORK_PARAMETERS_FILE = "network.par";
    public static final String DEFAULT_INPUT_SOLVER_PARAMETERS_FILE = "solvers.par";
    public static final String MODELS_OUTPUT_PARAMETERS_FILE = "models.par";
    public static final String NETWORK_OUTPUT_PARAMETERS_FILE = "network.par";
    public static final String SOLVER_OUTPUT_PARAMETERS_FILE = "solvers.par";
    public static final boolean DEFAULT_USE_MODEL_SIMPLIFIERS = false;
    public static final double DEFAULT_PRECISION = 1e-6;
    public static final ExportMode DEFAULT_TIMELINE_EXPORT_MODE = ExportMode.TXT;
    public static final LogLevel DEFAULT_LOG_LEVEL_FILTER = LogLevel.INFO;

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
    private SolverType solverType = DEFAULT_SOLVER_TYPE;
    private boolean mergeLoads = DEFAULT_MERGE_LOADS;
    private boolean useModelSimplifiers = DEFAULT_USE_MODEL_SIMPLIFIERS;
    private DumpFileParameters dumpFileParameters = DumpFileParameters.createDefaultDumpFileParameters();
    private double precision = DEFAULT_PRECISION;
    private ExportMode timelineExportMode = DEFAULT_TIMELINE_EXPORT_MODE;
    private LogLevel logLevelFilter = DEFAULT_LOG_LEVEL_FILTER;
    private EnumSet<SpecificLog> specificLogs = EnumSet.noneOf(SpecificLog.class);
    private Path criteriaFilePath = null;

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
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("dynawo-simulation-default-parameters");

        String parametersFile = config.flatMap(c -> c.getOptionalStringProperty("parametersFile")).orElse(DEFAULT_INPUT_PARAMETERS_FILE);
        String networkParametersFile = config.flatMap(c -> c.getOptionalStringProperty("network.parametersFile")).orElse(DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        String networkParametersId = config.flatMap(c -> c.getOptionalStringProperty("network.parametersId")).orElse(DEFAULT_NETWORK_PAR_ID);
        String solverParametersFile = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersFile")).orElse(DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);
        String solverParametersId = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersId")).orElse(DEFAULT_SOLVER_PAR_ID);
        // File with all the dynamic models' parameters for the simulation
        parameters.setModelsParameters(ParametersXml.load(resolveParameterPath(parametersFile, platformConfig, fileSystem)))
                // File with all the network's parameters for the simulation
                .setNetworkParameters(ParametersXml.load(resolveParameterPath(networkParametersFile, platformConfig, fileSystem), networkParametersId))
                // File with all the solvers' parameters for the simulation
                .setSolverParameters(ParametersXml.load(resolveParameterPath(solverParametersFile, platformConfig, fileSystem), solverParametersId));

        config.ifPresent(c -> {
            parameters.setDumpFileParameters(DumpFileParameters.createDumpFileParametersFromConfig(c, fileSystem));
            c.getOptionalEnumProperty("solver.type", SolverType.class).ifPresent(parameters::setSolverType);
            // If merging loads on each bus to simplify dynawo's analysis
            c.getOptionalBooleanProperty("mergeLoads").ifPresent(parameters::setMergeLoads);
            c.getOptionalBooleanProperty("useModelSimplifiers").ifPresent(parameters::setUseModelSimplifiers);
            c.getOptionalDoubleProperty("precision").ifPresent(parameters::setPrecision);
            c.getOptionalEnumProperty("timeline.exportMode", ExportMode.class).ifPresent(parameters::setTimelineExportMode);
            c.getOptionalEnumProperty("log.levelFilter", LogLevel.class).ifPresent(parameters::setLogLevelFilter);
            c.getOptionalEnumSetProperty("log.specificLogs", SpecificLog.class).ifPresent(parameters::setSpecificLogs);
            c.getOptionalStringProperty("criteria.file").ifPresent(cf -> parameters.setCriteriaFilePath(cf, fileSystem));
        });
        return parameters;
    }

    private static Path resolveParameterPath(String fileName, PlatformConfig platformConfig, FileSystem fileSystem) {
        return platformConfig.getConfigDir().map(configDir -> configDir.resolve(fileName)).orElse(fileSystem.getPath(fileName));
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

    public DynawoSimulationParameters setNetworkParameters(ParametersSet networkParameters) {
        this.networkParameters = Objects.requireNonNull(networkParameters);
        return this;
    }

    public ParametersSet getNetworkParameters() {
        return networkParameters;
    }

    public DynawoSimulationParameters setSolverParameters(ParametersSet solverParameters) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        return this;
    }

    public ParametersSet getSolverParameters() {
        return solverParameters;
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
        this.specificLogs = EnumSet.copyOf(specificLogs);
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
            throw new PowsyblException("File " + criteriaPath + " set in 'criteria.file' property cannot be found");
        }
        setCriteriaFilePath(criteriaPath);
    }
}

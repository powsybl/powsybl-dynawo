/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class DynaWaltzParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final String DEFAULT_NETWORK_PAR_ID = "1";
    public static final String DEFAULT_SOLVER_PAR_ID = "1";
    public static final boolean DEFAULT_MERGE_LOADS = true;
    public static final String DEFAULT_INPUT_PARAMETERS_FILE = "models.par";
    public static final String DEFAULT_INPUT_NETWORK_PARAMETERS_FILE = "network.par";
    public static final String DEFAULT_INPUT_SOLVER_PARAMETERS_FILE = "solvers.par";
    public static final String MODELS_OUTPUT_PARAMETERS_FILE = "models.par";
    public static final String NETWORK_OUTPUT_PARAMETERS_FILE = "network.par";
    public static final String SOLVER_OUTPUT_PARAMETERS_FILE = "solvers.par";
    private static final boolean DEFAULT_WRITE_FINAL_STATE = true;
    public static final boolean USE_MODEL_SIMPLIFIERS = false;
    public static final double DEFAULT_PRECISION = 1e-6;
    public static final ExportMode DEFAULT_TIMELINE_EXPORT_MODE = ExportMode.TXT;

    public enum SolverType {
        SIM,
        IDA
    }

    public enum ExportMode {
        CSV(".csv"),
        TXT(".log"),
        XML(".xml");

        private final String fileExtension;

        ExportMode(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }

    private Map<String, ParametersSet> modelsParameters = new LinkedHashMap<>();
    private ParametersSet networkParameters;
    private ParametersSet solverParameters;
    private SolverType solverType;
    private boolean mergeLoads;
    private boolean writeFinalState = DEFAULT_WRITE_FINAL_STATE;
    private boolean useModelSimplifiers = USE_MODEL_SIMPLIFIERS;
    private DumpFileParameters dumpFileParameters;
    private double precision = DEFAULT_PRECISION;
    private ExportMode timelineExportMode = DEFAULT_TIMELINE_EXPORT_MODE;

    /**
     * Loads parameters from the default platform configuration.
     */
    public static DynaWaltzParameters load() {
        return load(PlatformConfig.defaultConfig());
    }

    /**
     * Load parameters from a provided platform configuration.
     */
    public static DynaWaltzParameters load(PlatformConfig platformConfig) {
        return load(platformConfig, FileSystems.getDefault());
    }

    public static DynaWaltzParameters load(PlatformConfig platformConfig, FileSystem fileSystem) {
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("dynawaltz-default-parameters");

        // File with all the dynamic models' parameters for the simulation
        String parametersFile = config.map(c -> c.getStringProperty("parametersFile")).orElse(DEFAULT_INPUT_PARAMETERS_FILE);
        Path parametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(parametersFile))
                .orElse(fileSystem.getPath(parametersFile));

        // File with all the network's parameters for the simulation
        String networkParametersFile = config.map(c -> c.getStringProperty("network.parametersFile")).orElse(DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        Path networkParametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(networkParametersFile))
                .orElse(fileSystem.getPath(networkParametersFile));

        // Identifies the set of network parameters that will be used in the simulation.
        String networkParametersId = config.flatMap(c -> c.getOptionalStringProperty("network.parametersId")).orElse(DEFAULT_NETWORK_PAR_ID);

        // Information about the solver to use in the simulation, there are two options
        // the simplified solver
        // and the IDA solver
        SolverType solverType = config.flatMap(c -> c.getOptionalEnumProperty("solver.type", SolverType.class)).orElse(DEFAULT_SOLVER_TYPE);

        // File with all the solvers' parameters for the simulation
        String solverParametersFile = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersFile")).orElse(DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);
        Path solverParametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(solverParametersFile))
                .orElse(fileSystem.getPath(solverParametersFile));

        // Identifies the set of solver parameters that will be used in the simulation
        String solverParametersId = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersId")).orElse(DEFAULT_SOLVER_PAR_ID);

        // If merging loads on each bus to simplify dynawo's analysis
        boolean mergeLoads = config.flatMap(c -> c.getOptionalBooleanProperty("mergeLoads")).orElse(DEFAULT_MERGE_LOADS);

        // Writes final state IIDM
        boolean writeFinalState = config.flatMap(c -> c.getOptionalBooleanProperty("writeFinalState")).orElse(DEFAULT_WRITE_FINAL_STATE);

        boolean useModelSimplifiers = config.flatMap(c -> c.getOptionalBooleanProperty("useModelSimplifiers")).orElse(USE_MODEL_SIMPLIFIERS);

        // Dump file config
        boolean exportDumpFile = config.flatMap(c -> c.getOptionalBooleanProperty("dump.export")).orElse(DumpFileParameters.DEFAULT_EXPORT_DUMP);
        String exportDumpFileFolder = config.flatMap(c -> c.getOptionalStringProperty("dump.exportFolder")).orElse(DumpFileParameters.DEFAULT_DUMP_FOLDER);
        Path exportDumpFileFolderPath = exportDumpFileFolder != null ? fileSystem.getPath(exportDumpFileFolder) : null;
        boolean exportFolderNotFound = exportDumpFileFolderPath == null || !Files.exists(exportDumpFileFolderPath);
        if (exportDumpFile && exportFolderNotFound) {
            throw new PowsyblException("Folder " + exportDumpFileFolder + " set in 'dumpFileFolder' property cannot be found");
        }
        boolean useDumpFile = config.flatMap(c -> c.getOptionalBooleanProperty("dump.useAsInput")).orElse(DumpFileParameters.DEFAULT_USE_DUMP);
        String dumpFile = config.flatMap(c -> c.getOptionalStringProperty("dump.fileName")).orElse(DumpFileParameters.DEFAULT_DUMP_NAME);
        if (useDumpFile && (exportFolderNotFound || dumpFile == null || !Files.exists(exportDumpFileFolderPath.resolve(dumpFile)))) {
            throw new PowsyblException("File " + dumpFile + " set in 'dumpFile' property cannot be found");
        }
        DumpFileParameters dumpFileParameters = new DumpFileParameters(exportDumpFile, useDumpFile, exportDumpFileFolderPath, dumpFile);

        // Simulation precision
        Double precision = config.map(c -> c.getDoubleProperty("precision", DEFAULT_PRECISION)).orElse(DEFAULT_PRECISION);
      
        // Timeline export mode
        ExportMode timelineExport = config.flatMap(c -> c.getOptionalEnumProperty("timeline.exportMode", ExportMode.class)).orElse(DEFAULT_TIMELINE_EXPORT_MODE);

        // Load xml files
        List<ParametersSet> modelsParameters = ParametersXml.load(parametersPath);
        ParametersSet networkParameters = ParametersXml.load(networkParametersPath, networkParametersId);
        ParametersSet solverParameters = ParametersXml.load(solverParametersPath, solverParametersId);

        return new DynaWaltzParameters()
                .setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(solverType)
                .setMergeLoads(mergeLoads)
                .setWriteFinalState(writeFinalState)
                .setUseModelSimplifiers(useModelSimplifiers)
                .setDumpFileParameters(dumpFileParameters)
                .setPrecision(precision);
                .setTimelineExportMode(timelineExport);
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
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
    public DynaWaltzParameters setModelsParameters(Collection<ParametersSet> parametersSets) {
        modelsParameters = new LinkedHashMap<>();
        parametersSets.forEach(parametersSet -> modelsParameters.put(parametersSet.getId(), parametersSet));
        return this;
    }

    public DynaWaltzParameters setNetworkParameters(ParametersSet networkParameters) {
        this.networkParameters = Objects.requireNonNull(networkParameters);
        return this;
    }

    public ParametersSet getNetworkParameters() {
        return networkParameters;
    }

    public DynaWaltzParameters setSolverParameters(ParametersSet solverParameters) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        return this;
    }

    public ParametersSet getSolverParameters() {
        return solverParameters;
    }

    public DynaWaltzParameters setSolverType(SolverType solverType) {
        this.solverType = solverType;
        return this;
    }

    public SolverType getSolverType() {
        return solverType;
    }

    public boolean isMergeLoads() {
        return mergeLoads;
    }

    public DynaWaltzParameters setMergeLoads(boolean mergeLoads) {
        this.mergeLoads = mergeLoads;
        return this;
    }

    public DynaWaltzParameters setWriteFinalState(boolean writeFinalState) {
        this.writeFinalState = writeFinalState;
        return this;
    }

    public boolean isWriteFinalState() {
        return writeFinalState;
    }

    public boolean isUseModelSimplifiers() {
        return useModelSimplifiers;
    }

    public DynaWaltzParameters setUseModelSimplifiers(boolean useModelSimplifiers) {
        this.useModelSimplifiers = useModelSimplifiers;
        return this;
    }

    public DumpFileParameters getDumpFileParameters() {
        return dumpFileParameters;
    }

    public DynaWaltzParameters setDumpFileParameters(DumpFileParameters dumpFileParameters) {
        this.dumpFileParameters = dumpFileParameters;
        return this;
    }

    public DynaWaltzParameters setDefaultDumpFileParameters() {
        this.dumpFileParameters = DumpFileParameters.createDefaultDumpFileParameters();
        return this;
    }

    public double getPrecision() {
        return precision;
    }

    public DynaWaltzParameters setPrecision(double precision) {
        this.precision = precision;
        return this;
    }
      
    public ExportMode getTimelineExportMode() {
        return timelineExportMode;
    }

    public DynaWaltzParameters setTimelineExportMode(ExportMode timelineExportMode) {
        this.timelineExportMode = timelineExportMode;
        return this;
    }
}

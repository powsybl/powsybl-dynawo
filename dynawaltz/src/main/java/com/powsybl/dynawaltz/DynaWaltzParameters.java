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
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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
    public static final boolean DEFAULT_MERGE_LOADS = false;
    public static final String DEFAULT_INPUT_PARAMETERS_FILE = "models.par";
    public static final String DEFAULT_INPUT_NETWORK_PARAMETERS_FILE = "network.par";
    public static final String DEFAULT_INPUT_SOLVER_PARAMETERS_FILE = "solvers.par";
    public static final String MODELS_OUTPUT_PARAMETERS_FILE = "models.par";
    public static final String NETWORK_OUTPUT_PARAMETERS_FILE = "network.par";
    public static final String SOLVER_OUTPUT_PARAMETERS_FILE = "solvers.par";
    private static final boolean DEFAULT_WRITE_FINAL_STATE = true;
    public static final boolean DEFAULT_USE_MODEL_SIMPLIFIERS = false;
    public static final double DEFAULT_PRECISION = 1e-6;
    public static final ExportMode DEFAULT_TIMELINE_EXPORT_MODE = ExportMode.TXT;

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
    private SolverType solverType = DEFAULT_SOLVER_TYPE;
    private boolean mergeLoads = DEFAULT_MERGE_LOADS;
    private boolean writeFinalState = DEFAULT_WRITE_FINAL_STATE;
    private boolean useModelSimplifiers = DEFAULT_USE_MODEL_SIMPLIFIERS;
    private DumpFileParameters dumpFileParameters = DumpFileParameters.DEFAULT_DUMP_FILE_PARAMETERS;
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
        DynaWaltzParameters parameters = new DynaWaltzParameters();
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("dynawaltz-default-parameters");

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
            c.getOptionalBooleanProperty("writeFinalState").ifPresent(parameters::setWriteFinalState);
            c.getOptionalBooleanProperty("useModelSimplifiers").ifPresent(parameters::setUseModelSimplifiers);
            c.getOptionalDoubleProperty("precision").ifPresent(parameters::setPrecision);
            c.getOptionalEnumProperty("timeline.exportMode", ExportMode.class).ifPresent(parameters::setTimelineExportMode);
        });
        return parameters;
    }

    private static Path resolveParameterPath(String fileName, PlatformConfig platformConfig, FileSystem fileSystem) {
        return platformConfig.getConfigDir().map(configDir -> configDir.resolve(fileName)).orElse(fileSystem.getPath(fileName));
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

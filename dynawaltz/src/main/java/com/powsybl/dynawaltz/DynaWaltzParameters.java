/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.powsybl.commons.config.ModuleConfig;
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
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
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

    public enum SolverType {
        SIM,
        IDA
    }

    private Map<String, ParametersSet> modelsParameters = new LinkedHashMap<>();
    private ParametersSet networkParameters;
    private ParametersSet solverParameters;
    private SolverType solverType;
    private boolean mergeLoads = false;
    private boolean writeFinalState = DEFAULT_WRITE_FINAL_STATE;

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
                .setWriteFinalState(writeFinalState);
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
    }

    public ParametersSet getModelParameters(String parameterSetId) {
        return modelsParameters.get(parameterSetId);
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
}

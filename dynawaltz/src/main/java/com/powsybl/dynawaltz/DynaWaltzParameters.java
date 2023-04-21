/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.parameters.Set;
import com.powsybl.dynawaltz.xml.ParametersXml;

import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final String DEFAULT_NETWORK_PAR_ID = "1";
    public static final String DEFAULT_SOLVER_PAR_ID = "1";
    public static final boolean DEFAULT_MERGE_LOADS = true;
    public static final String DEFAULT_PARAMETERS_FILE = "models.par";
    public static final String DEFAULT_NETWORK_PARAMETERS_FILE = "network.par";
    public static final String DEFAULT_SOLVER_PARAMETERS_FILE = "solvers.par";
    public static final String MODELS_OUTPUT_PARAMETERS_FILE = "modelsOut.par";
    public static final String NETWORK_OUTPUT_PARAMETERS_FILE = "networkOut.par";
    public static final String SOLVER_OUTPUT_PARAMETERS_FILE = "solversOut.par";

    public enum SolverType {
        SIM,
        IDA
    }

    private Map<String, Set> modelsParameters;
    private Set networkParameters;
    private Set solverParameters;
    private SolverType solverType;
    private boolean mergeLoads;

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
        String parametersFile = config.map(c -> c.getStringProperty("parametersFile")).orElse(DEFAULT_PARAMETERS_FILE);
        Path parametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(parametersFile))
                .orElse(fileSystem.getPath(parametersFile));

        // File with all the network's parameters for the simulation
        String networkParametersFile = config.map(c -> c.getStringProperty("network.parametersFile")).orElse(DEFAULT_NETWORK_PARAMETERS_FILE);
        Path networkParametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(networkParametersFile))
                .orElse(fileSystem.getPath(networkParametersFile));

        // Identifies the set of network parameters that will be used in the simulation.
        String networkParametersId = config.flatMap(c -> c.getOptionalStringProperty("network.parametersId")).orElse(DEFAULT_NETWORK_PAR_ID);

        // Information about the solver to use in the simulation, there are two options
        // the simplified solver
        // and the IDA solver
        SolverType solverType = config.flatMap(c -> c.getOptionalEnumProperty("solver.type", SolverType.class)).orElse(DEFAULT_SOLVER_TYPE);

        // File with all the solvers' parameters for the simulation
        String solverParametersFile = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersFile")).orElse(DEFAULT_SOLVER_PARAMETERS_FILE);
        Path solverParametersPath = platformConfig.getConfigDir().map(configDir -> configDir.resolve(solverParametersFile))
                .orElse(fileSystem.getPath(solverParametersFile));

        // Identifies the set of solver parameters that will be used in the simulation
        String solverParametersId = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersId")).orElse(DEFAULT_SOLVER_PAR_ID);

        // If merging loads on each bus to simplify dynawo's analysis
        boolean mergeLoads = config.flatMap(c -> c.getOptionalBooleanProperty("mergeLoads")).orElse(DEFAULT_MERGE_LOADS);

        return new DynaWaltzParameters()
                .setModelsParameters(parametersPath)
                .setNetworkParameters(networkParametersPath, networkParametersId)
                .setSolverParameters(solverParametersPath, solverParametersId)
                .setSolverType(solverType)
                .setMergeLoads(mergeLoads);
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
    }

    public Set getModelParameters(String parameterSetId) {
        return modelsParameters.get(parameterSetId);
    }

    public Collection<Set> getModelParameters() {
        return modelsParameters.values();
    }

    @JsonIgnore
    public DynaWaltzParameters setModelsParameters(Path modelsParametersFile) {
        this.modelsParameters = ParametersXml.load(modelsParametersFile);
        return this;
    }

    @JsonIgnore
   public DynaWaltzParameters setModelsParameters(InputStream modelsParametersFile) {
        this.modelsParameters = ParametersXml.load(modelsParametersFile);
        return this;
    }

    public DynaWaltzParameters setNetworkParameters(Path networkParameters, String networkParametersId) {
        this.networkParameters = ParametersXml.load(networkParameters).getOrDefault(networkParametersId, new Set(networkParametersId));
        return this;
    }
    
    public DynaWaltzParameters setNetworkParameters(InputStream networkParameters, String networkParametersId) {
        this.networkParameters = ParametersXml.load(networkParameters).getOrDefault(networkParametersId, new Set(networkParametersId));
        return this;
    }

    public DynaWaltzParameters setNetworkParameters(Set networkParameters) {
        this.networkParameters = Objects.requireNonNull(networkParameters);
        return this;
    }

    public Set getNetworkParameters() {
        return networkParameters;
    }

    public DynaWaltzParameters setSolverParameters(Path solverParameters, String solverParametersId) {
        this.solverParameters = ParametersXml.load(solverParameters).getOrDefault(solverParametersId, new Set(solverParametersId));
        return this;
    }

    public DynaWaltzParameters setSolverParameters(InputStream solverParameters, String solverParametersId) {
        this.solverParameters = ParametersXml.load(solverParameters).getOrDefault(solverParametersId, new Set(solverParametersId));
        return this;
    }

    public DynaWaltzParameters setSolverParameters(Set solverParameters) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        return this;
    }

    public Set getSolverParameters() {
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
}

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

    public Collection<Set> getNetworkParameterSets() {
        return network.getParametersSet().values();
    }

    public Collection<Set> getSolverParameterSets() {
        return solver.getParametersSet().values();
    }

    public enum SolverType {
        SIM,
        IDA
    }

    public static class Network {

        private String parametersId;
        private Map<String, Set> parametersSet;

        public Network() {
        }

        public Map<String, Set> getParametersSet() {
            return parametersSet;
        }

        @JsonIgnore
        public Network setParameters(Path parametersFile) {
            this.parametersSet = ParametersXml.load(parametersFile);
            return this;
        }

        @JsonIgnore
        public Network setParameters(InputStream parametersFile) {
            this.parametersSet = ParametersXml.load(parametersFile);
            return this;
        }

        public String getParametersId() {
            return parametersId;
        }

        public Network setParametersId(String parametersId) {
            this.parametersId = Objects.requireNonNull(parametersId);
            return this;
        }
    }

    public static class Solver {

        private String parametersId;
        private SolverType type;
        private Map<String, Set> parametersSet;

        public Solver() {
        }

        public SolverType getType() {
            return type;
        }

        public Solver setType(SolverType type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Map<String, Set> getParametersSet() {
            return parametersSet;
        }

        @JsonIgnore
        public Solver setParameters(Path parametersFile) {
            this.parametersSet = ParametersXml.load(parametersFile);
            return this;
        }

        @JsonIgnore
        public Solver setParameters(InputStream parametersFile) {
            this.parametersSet = ParametersXml.load(parametersFile);
            return this;
        }

        public String getParametersId() {
            return parametersId;
        }

        public Solver setParametersId(String parametersId) {
            this.parametersId = Objects.requireNonNull(parametersId);
            return this;
        }
    }

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
                .setParameters(parametersPath)
                .setNetwork(new Network().setParametersId(networkParametersId).setParameters(networkParametersPath))
                .setSolver(new Solver().setParametersId(solverParametersId).setType(solverType).setParameters(solverParametersPath))
                .setMergeLoads(mergeLoads);
    }

    private Map<String, Set> modelsParameters;
    private Network network;
    private Solver solver;
    private boolean mergeLoads;

    public DynaWaltzParameters() {
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
    }

    public Set getModelParameterSet(String parameterSetId) {
        return modelsParameters.get(parameterSetId);
    }

    public Collection<Set> getModelParameterSets() {
        return modelsParameters.values();
    }

    @JsonIgnore
    public DynaWaltzParameters setParameters(Path modelsParametersFile) {
        this.modelsParameters = ParametersXml.load(modelsParametersFile);
        return this;
    }

    @JsonIgnore
   public DynaWaltzParameters setParameters(InputStream modelsParametersFile) {
        this.modelsParameters = ParametersXml.load(modelsParametersFile);
        return this;
    }

    public Network getNetwork() {
        return network;
    }

    public DynaWaltzParameters setNetwork(Network network) {
        this.network = Objects.requireNonNull(network);
        return this;
    }

    public Solver getSolver() {
        return solver;
    }

    public DynaWaltzParameters setSolver(Solver solver) {
        this.solver = Objects.requireNonNull(solver);
        return this;
    }

    public boolean isMergeLoads() {
        return mergeLoads;
    }

    public DynaWaltzParameters setMergeLoads(boolean mergeLoads) {
        this.mergeLoads = mergeLoads;
        return this;
    }
}

/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;

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

    public enum SolverType {
        SIM,
        IDA
    }

    public static class Network {

        private String parametersId;
        private ParametersSet parameters;

        public Network() {
        }

        public ParametersSet getParameters() {
            return parameters;
        }

        public Network setParameters(ParametersSet parameters) {
            this.parameters = Objects.requireNonNull(parameters);
            return this;
        }

        public Network setParameters(Path parametersFile) {
            this.parameters = ParametersSet.load(parametersFile);
            return this;
        }

        public Network setParameters(InputStream parametersFile) {
            this.parameters = ParametersSet.load(parametersFile);
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
        private ParametersSet parameters;

        public Solver() {
        }

        public SolverType getType() {
            return type;
        }

        public Solver setType(SolverType type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public ParametersSet getParameters() {
            return parameters;
        }

        public Solver setParameters(ParametersSet parameters) {
            this.parameters = Objects.requireNonNull(parameters);
            return this;
        }

        public Solver setParameters(Path parametersFile) {
            this.parameters = ParametersSet.load(parametersFile);
            return this;
        }

        public Solver setParameters(InputStream parametersFile) {
            this.parameters = ParametersSet.load(parametersFile);
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

    protected static DynaWaltzParameters load(PlatformConfig platformConfig, FileSystem fileSystem) {
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("dynawaltz-default-parameters");

        // File with all the dynamic models' parameters for the simulation
        String parametersFile = config.map(c -> c.getStringProperty("parametersFile")).orElse(DEFAULT_PARAMETERS_FILE);

        // File with all the network's parameters for the simulation
        String networkParametersFile = config.map(c -> c.getStringProperty("network.parametersFile")).orElse(DEFAULT_NETWORK_PARAMETERS_FILE);

        // Identifies the set of network parameters that will be used in the simulation.
        String networkParametersId = config.flatMap(c -> c.getOptionalStringProperty("network.parametersId")).orElse(DEFAULT_NETWORK_PAR_ID);

        // Information about the solver to use in the simulation, there are two options
        // the simplified solver
        // and the IDA solver
        SolverType solverType = config.flatMap(c -> c.getOptionalEnumProperty("solver.type", SolverType.class)).orElse(DEFAULT_SOLVER_TYPE);

        // File with all the solvers' parameters for the simulation
        String solverParametersFile = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersFile")).orElse(DEFAULT_SOLVER_PARAMETERS_FILE);

        // Identifies the set of solver parameters that will be used in the simulation
        String solverParametersId = config.flatMap(c -> c.getOptionalStringProperty("solver.parametersId")).orElse(DEFAULT_SOLVER_PAR_ID);

        // If merging loads on each bus to simplify dynawo's analysis
        boolean mergeLoads = config.flatMap(c -> c.getOptionalBooleanProperty("mergeLoads")).orElse(DEFAULT_MERGE_LOADS);

        return new DynaWaltzParameters()
                .setParametersFile(fileSystem.getPath(parametersFile))
                .setNetwork(new Network().setParametersId(networkParametersId).setParameters(fileSystem.getPath(networkParametersFile)))
                .setSolver(new Solver().setParametersId(solverParametersId).setType(solverType).setParameters(fileSystem.getPath(solverParametersFile)))
                .setMergeLoads(mergeLoads);
    }

    private ParametersSet modelsParameters;
    private Network network;
    private Solver solver;
    private boolean mergeLoads;

    public DynaWaltzParameters() {
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
    }

    public ParametersSet getModelsParameters() {
        return modelsParameters;
    }

    public DynaWaltzParameters setParametersFile(ParametersSet modelsParameters) {
        this.modelsParameters = Objects.requireNonNull(modelsParameters);
        return this;
    }

    public DynaWaltzParameters setParametersFile(Path modelsParametersFile) {
        this.modelsParameters = ParametersSet.load(modelsParametersFile);
        return this;
    }

   public DynaWaltzParameters setParametersFile(InputStream modelsParametersFile) {
        this.modelsParameters = ParametersSet.load(modelsParametersFile);
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

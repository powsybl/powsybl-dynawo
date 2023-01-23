/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

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
    private static final String DEFAULT_PARAMETERS_FILE = "models.par";
    private static final String DEFAULT_NETWORK_PARAMETERS_FILE = "network.par";
    private static final String DEFAULT_SOLVER_PARAMETERS_FILE = "solvers.par";

    public enum SolverType {
        SIM,
        IDA
    }

    public static class Network {

        public Network() {
        }

        public Network(String parametersFile, String parametersId) {
            this.parametersFile = Objects.requireNonNull(parametersFile);
            this.parametersId = Objects.requireNonNull(parametersId);
        }

        public String getParametersFile() {
            return parametersFile;
        }

        public void setParametersFile(String parametersFile) {
            this.parametersFile = Objects.requireNonNull(parametersFile);
        }

        public String getParametersId() {
            return parametersId;
        }

        public void setParametersId(String parametersId) {
            this.parametersId = Objects.requireNonNull(parametersId);
        }

        private String parametersFile;
        private String parametersId;
    }

    public static class Solver {

        public Solver() {
        }

        public Solver(SolverType type, String parametersFile, String parametersId) {
            this.type = Objects.requireNonNull(type);
            this.parametersFile = Objects.requireNonNull(parametersFile);
            this.parametersId = Objects.requireNonNull(parametersId);
        }

        public SolverType getType() {
            return type;
        }

        public void setType(SolverType type) {
            this.type = Objects.requireNonNull(type);
        }

        public String getParametersFile() {
            return parametersFile;
        }

        public void setParametersFile(String parametersFile) {
            this.parametersFile = Objects.requireNonNull(parametersFile);
        }

        public String getParametersId() {
            return parametersId;
        }

        public void setParametersId(String parametersId) {
            this.parametersId = Objects.requireNonNull(parametersId);
        }

        private SolverType type;
        private String parametersFile;
        private String parametersId;
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

        return new DynaWaltzParameters(parametersFile, networkParametersFile, networkParametersId, solverType, solverParametersFile, solverParametersId);
    }

    public DynaWaltzParameters() {
    }

    public DynaWaltzParameters(String parametersFile, String networkParametersFile, String networkParametersId, SolverType solverType, String solverParametersFile,
                            String solverParametersId) {
        this.parametersFile = Objects.requireNonNull(parametersFile);
        this.network = new Network(networkParametersFile, networkParametersId);
        this.solver = new Solver(solverType, solverParametersFile, solverParametersId);
    }

    @Override
    public String getName() {
        return "DynaWaltzParameters";
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public void setParametersFile(String parametersFile) {
        this.parametersFile = Objects.requireNonNull(parametersFile);
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = Objects.requireNonNull(network);
    }

    public Solver getSolver() {
        return solver;
    }

    public void setSolver(Solver solver) {
        this.solver = Objects.requireNonNull(solver);
    }

    private String parametersFile;
    private Network network;
    private Solver solver;
}

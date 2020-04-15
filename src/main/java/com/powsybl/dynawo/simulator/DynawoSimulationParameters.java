/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.Objects;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final String DEFAULT_NETWORK_PAR_ID = "NETWORK";
    public static final String DEFAULT_SOLVER_PAR_ID = "SIM";

    public enum SolverType {
        SIM,
        IDA
    }

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
        ModuleConfig config = platformConfig.getModuleConfig("dynawo-default-parameters");
        // File with all the dynamic models' parameters for the simulation
        String parametersFile = config.getStringProperty("parametersFile");
        // Identifies the set of network parameters that will be used in the simulation.
        String networkParametersId = config.getStringProperty("network.ParametersId", DEFAULT_NETWORK_PAR_ID);
        // Information about the solver to use in the simulation, there are two options the simplified solver
        //  and the IDA solver
        SolverType solverType = config.getEnumProperty("solver.type", SolverType.class, DEFAULT_SOLVER_TYPE);
        // File with all the solvers' parameters for the simulation
        String solverParametersFile = config.getStringProperty("solver.parametersFile");
        // Identifies the set of solver parameters that will be used in the simulation
        String solverParametersId = config.getStringProperty("solver.parametersId", DEFAULT_SOLVER_PAR_ID);

        return new DynawoSimulationParameters(parametersFile, networkParametersId, solverType, solverParametersFile, solverParametersId);
    }

    public DynawoSimulationParameters(String parametersFile, String networkParametersId, SolverType solverType, String solverParametersFile,
        String solverParametersId) {
        this.parametersFile = Objects.requireNonNull(parametersFile);
        this.networkParametersId = Objects.requireNonNull(networkParametersId);
        this.solverType = Objects.requireNonNull(solverType);
        this.solverParametersFile = Objects.requireNonNull(solverParametersFile);
        this.solverParametersId = Objects.requireNonNull(solverParametersId);
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public String getNetworkParametersId() {
        return networkParametersId;
    }

    public SolverType getSolverType() {
        return solverType;
    }

    public String getSolverParametersFile() {
        return solverParametersFile;
    }

    public String getSolverParametersId() {
        return solverParametersId;
    }

    private final String parametersFile;
    private final String networkParametersId;
    private final SolverType solverType;
    private final String solverParametersFile;
    private final String solverParametersId;
}

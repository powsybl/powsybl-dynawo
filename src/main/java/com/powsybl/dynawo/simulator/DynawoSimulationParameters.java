/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters {

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
        String parametersDDB = config.getStringProperty("parametersDDB");
        // Identifies the set of network parameters that will be used in the simulation.
        String networkParametersId = config.getStringProperty("networkParametersId");

        config = platformConfig.getModuleConfig("dynawo-solver-default-parameters");
        // Information about the solver to use in the simulation, there are two options the simplified solver
        //  and the IDA solver
        SolverType solverType = config.getEnumProperty("type", SolverType.class);
        // File with all the solvers' parameters for the simulation
        String solverParametersFile = config.getStringProperty("parametersFile");
        // Identifies the set of solver parameters that will be used in the simulation
        String solverParametersId = config.getStringProperty("parametersId");

        return new DynawoSimulationParameters(parametersDDB, networkParametersId, solverType, solverParametersFile, solverParametersId);
    }

    public DynawoSimulationParameters(String parametersDDB, String networkParametersId, SolverType solverType, String solverParametersFile,
        String solverParametersId) {
        this.parametersDDB = parametersDDB;
        this.networkParametersId = networkParametersId;
        this.solverType = solverType;
        this.solverParametersFile = solverParametersFile;
        this.solverParametersId = solverParametersId;
    }

    public String getParametersDDB() {
        return parametersDDB;
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

    private final String parametersDDB;
    private final String networkParametersId;
    private final SolverType solverType;
    private final String solverParametersFile;
    private final String solverParametersId;
}

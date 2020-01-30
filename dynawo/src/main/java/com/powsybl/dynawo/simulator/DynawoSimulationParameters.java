/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.Objects;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoInputProvider;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final int DEFAULT_IDA_ORDER = 2;

    public enum SolverType {
        SIM,
        IDA
    }

    public static class SolverParameters {
        private final SolverType type;

        public SolverParameters(SolverType type) {
            this.type = type;
        }

        public SolverType getType() {
            return type;
        }
    }

    public static class SolverIDAParameters extends SolverParameters {
        private final int order;

        public SolverIDAParameters(int order) {
            super(SolverType.IDA);
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
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
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        load(parameters, platformConfig);

        return parameters;
    }

    protected static void load(DynawoSimulationParameters parameters) {
        load(parameters, PlatformConfig.defaultConfig());
    }

    protected static void load(DynawoSimulationParameters parameters, PlatformConfig platformConfig) {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(platformConfig);

        platformConfig.getOptionalModuleConfig("dynawo-simulation-default-parameters")
            .ifPresent(config -> {
                SolverParameters solverParameters = null;
                SolverType solverType = config.getEnumProperty("solver", SolverType.class, DEFAULT_SOLVER_TYPE);
                if (solverType.equals(SolverType.IDA)) {
                    solverParameters = new SolverIDAParameters(config.getIntProperty("IDAorder", DEFAULT_IDA_ORDER));
                } else {
                    solverParameters = new SolverParameters(solverType);
                }
                parameters.setSolverParameters(solverParameters);
            });
    }

    public DynawoSimulationParameters() {
        this(new SolverParameters(DEFAULT_SOLVER_TYPE), null, null);
    }

    public DynawoSimulationParameters(SolverParameters solverParameters, String dslFilename) {
        this(solverParameters, dslFilename, null);
    }

    public DynawoSimulationParameters(SolverParameters solverParameters, DynawoInputProvider dynawoInputProvider) {
        this(solverParameters, null, dynawoInputProvider);
    }

    private DynawoSimulationParameters(SolverParameters solverParameters, String dslFilename, DynawoInputProvider dynawoInputProvider) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        this.dslFilename = dslFilename;
        this.dynawoInputProvider = dynawoInputProvider;
    }

    @Override
    public String getName() {
        return "DynawoSimulationParameters";
    }

    public SolverParameters getSolverParameters() {
        return solverParameters;
    }

    public DynawoSimulationParameters setSolverParameters(SolverParameters solverParameters) {
        this.solverParameters = Objects.requireNonNull(solverParameters);
        return this;
    }

    public DynawoInputProvider getDynawoInputProvider() {
        return dynawoInputProvider;
    }

    public DynawoSimulationParameters setDynawoInputProvider(DynawoInputProvider dynawoInputProvider) {
        this.dynawoInputProvider = dynawoInputProvider;
        return this;
    }

    public String getDslFilename() {
        return dslFilename;
    }

    public DynawoSimulationParameters setDslFilename(String dslFilename) {
        this.dslFilename = dslFilename;
        return this;
    }

    private SolverParameters solverParameters;
    private String dslFilename;

    private DynawoInputProvider dynawoInputProvider;

}

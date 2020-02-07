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
import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.dynawo.inputs.model.DynawoInputsProvider;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParameters extends AbstractExtension<DynamicSimulationParameters> {

    public static final SolverType DEFAULT_SOLVER_TYPE = SolverType.SIM;
    public static final int DEFAULT_IDA_ORDER = 2;
    public static final double DEFAULT_IDA_MIN_STEP = 0.000001;
    public static final double DEFAULT_IDA_MAX_STEP = 10;
    public static final double DEFAULT_IDA_ACCURACY = 1e-4;
    public static final double DEFAULT_SIM_H_MIN = 0.000001;
    public static final double DEFAULT_SIM_H_MAX = 1;
    public static final double DEFAULT_SIM_K_REDUCE_STEP = 0.5;
    public static final int DEFAULT_SIM_N_EFF = 10;
    public static final int DEFAULT_SIM_N_DEADBAND = 2;
    public static final int DEFAULT_SIM_MAX_ROOT_RESTART = 3;
    public static final int DEFAULT_SIM_MAX_NEWTON_TRY = 10;
    public static final String DEFAULT_SIM_LINEAR_SOLVER_NAME = "KLU";
    public static final boolean DEFAULT_SIM_RECALCULATE_STEP = false;
    public static final SolverParameters DEFAULT_SOLVER_PARAMETERS = new SolverSIMParameters(DEFAULT_SIM_H_MIN,
        DEFAULT_SIM_H_MAX, DEFAULT_SIM_K_REDUCE_STEP, DEFAULT_SIM_N_EFF, DEFAULT_SIM_N_DEADBAND, DEFAULT_SIM_MAX_ROOT_RESTART,
        DEFAULT_SIM_MAX_NEWTON_TRY, DEFAULT_SIM_LINEAR_SOLVER_NAME, DEFAULT_SIM_RECALCULATE_STEP);

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

    public static class SolverSIMParameters extends SolverParameters {
        private final double hMin;
        private final double hMax;
        private final double kReduceStep;
        private final int nEff;
        private final int nDeadBand;
        private final int maxRootRestart;
        private final int maxNewtonTry;
        private final String linearSolverName;
        private final boolean recalculateStep;

        public SolverSIMParameters(double hMin, double hMax, double kReduceStep, int nEff,
            int nDeadBand, int maxRootRestart, int maxNewtonTry, String linearSolverName, boolean recalculateStep) {
            super(SolverType.SIM);
            this.hMin = hMin;
            this.hMax = hMax;
            this.kReduceStep = kReduceStep;
            this.nEff = nEff;
            this.nDeadBand = nDeadBand;
            this.maxRootRestart = maxRootRestart;
            this.maxNewtonTry = maxNewtonTry;
            this.linearSolverName = linearSolverName;
            this.recalculateStep = recalculateStep;
        }

        public double gethMin() {
            return hMin;
        }

        public double gethMax() {
            return hMax;
        }

        public double getkReduceStep() {
            return kReduceStep;
        }

        public int getnEff() {
            return nEff;
        }

        public int getnDeadBand() {
            return nDeadBand;
        }

        public int getMaxRootRestart() {
            return maxRootRestart;
        }

        public int getMaxNewtonTry() {
            return maxNewtonTry;
        }

        public String getLinearSolverName() {
            return linearSolverName;
        }

        public boolean recalculateStep() {
            return recalculateStep;
        }
    }

    public static class SolverIDAParameters extends SolverParameters {
        private final int order;
        private final double initStep;
        private final double minStep;
        private final double maxStep;
        private final double relAccuracy;
        private final double absAccuracy;

        public SolverIDAParameters(int order, double initStep, double minStep, double maxStep, double relAccuracy, double absAccuracy) {
            super(SolverType.IDA);
            this.order = order;
            this.initStep = initStep;
            this.minStep = minStep;
            this.maxStep = maxStep;
            this.relAccuracy = relAccuracy;
            this.absAccuracy = absAccuracy;
        }

        public int getOrder() {
            return order;
        }

        public double getInitStep() {
            return initStep;
        }

        public double getMinStep() {
            return minStep;
        }

        public double getMaxStep() {
            return maxStep;
        }

        public double getRelAccuracy() {
            return relAccuracy;
        }

        public double getAbsAccuracy() {
            return absAccuracy;
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
                    solverParameters = new SolverIDAParameters(config.getIntProperty("IDAorder", DEFAULT_IDA_ORDER),
                        config.getDoubleProperty("IDAinitStep", DEFAULT_IDA_MIN_STEP),
                        config.getDoubleProperty("IDAminStep", DEFAULT_IDA_MIN_STEP),
                        config.getDoubleProperty("IDAmaxStep", DEFAULT_IDA_MAX_STEP),
                        config.getDoubleProperty("IDArelAccuracy", DEFAULT_IDA_ACCURACY),
                        config.getDoubleProperty("IDAabsAccuracy", DEFAULT_IDA_ACCURACY));
                } else if (solverType.equals(SolverType.SIM)) {
                    solverParameters = new SolverSIMParameters(config.getDoubleProperty("SIMhMin", DEFAULT_SIM_H_MIN),
                        config.getDoubleProperty("SIMhMax", DEFAULT_SIM_H_MAX),
                        config.getDoubleProperty("SIMkReduceStep", DEFAULT_SIM_K_REDUCE_STEP),
                        config.getIntProperty("SIMnEff", DEFAULT_SIM_N_EFF),
                        config.getIntProperty("SIMnDeadband", DEFAULT_SIM_N_DEADBAND),
                        config.getIntProperty("SIMmaxRootRestart", DEFAULT_SIM_MAX_ROOT_RESTART),
                        config.getIntProperty("SIMmaxNewtonTry", DEFAULT_SIM_MAX_NEWTON_TRY),
                        config.getStringProperty("SIMlinearSolverName", DEFAULT_SIM_LINEAR_SOLVER_NAME),
                        config.getBooleanProperty("SIMrecalculateStep", DEFAULT_SIM_RECALCULATE_STEP));
                } else {
                    solverParameters = DEFAULT_SOLVER_PARAMETERS;
                }
                parameters.setSolverParameters(solverParameters);
            });
    }

    public DynawoSimulationParameters() {
        this(DEFAULT_SOLVER_PARAMETERS, null, null);
    }

    public DynawoSimulationParameters(SolverParameters solverParameters, String dslFilename) {
        this(solverParameters, dslFilename, null);
    }

    public DynawoSimulationParameters(SolverParameters solverParameters, DynawoInputsProvider dynawoInputProvider) {
        this(solverParameters, null, dynawoInputProvider);
    }

    private DynawoSimulationParameters(SolverParameters solverParameters, String dslFilename, DynawoInputsProvider dynawoInputProvider) {
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

    public DynawoInputs getDynawoInputs() {
        return dynawoInputs;
    }

    public DynawoSimulationParameters setDynawoInputs(DynawoInputs dynawoInputs) {
        this.dynawoInputs = dynawoInputs;
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

    private DynawoInputsProvider dynawoInputProvider;

    // FIXME Used only for initial testing, should be removed
    private DynawoInputs dynawoInputs;
}

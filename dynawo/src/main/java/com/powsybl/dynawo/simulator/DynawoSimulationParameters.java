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

    public static final Solver DEFAULT_SOLVER = Solver.SIM;
    public static final int DEFAULT_IDA_ORDER = 2;

    public enum Solver {
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
                parameters.setSolver(config.getEnumProperty("solver", Solver.class, DEFAULT_SOLVER));
                parameters.setIdaOrder(config.getIntProperty("IDAorder", DEFAULT_IDA_ORDER));
            });
    }

    public DynawoSimulationParameters() {
        this(DEFAULT_SOLVER, DEFAULT_IDA_ORDER, null, null);
    }

    public DynawoSimulationParameters(Solver solver, int order, String dslFilename) {
        this(solver, order, dslFilename, null);
    }

    public DynawoSimulationParameters(Solver solver, int order, DynawoInputProvider dynawoInputProvider) {
        this(solver, order, null, dynawoInputProvider);
    }

    private DynawoSimulationParameters(Solver solver, int order, String dslFilename, DynawoInputProvider dynawoInputProvider) {
        this.solver = Objects.requireNonNull(solver);
        this.idaOrder = order;
        this.dslFilename = dslFilename;
        this.dynawoInputProvider = dynawoInputProvider;
    }

    @Override
    public String getName() {
        return "DynawoSimulationParameters";
    }

    public Solver getSolver() {
        return solver;
    }

    public DynawoSimulationParameters setSolver(Solver solver) {
        this.solver = Objects.requireNonNull(solver);
        return this;
    }

    public int getIdaOrder() {
        return idaOrder;
    }

    public DynawoSimulationParameters setIdaOrder(int order) {
        this.idaOrder = order;
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

    private Solver solver;
    private int idaOrder;
    private String dslFilename;

    private DynawoInputProvider dynawoInputProvider;

}

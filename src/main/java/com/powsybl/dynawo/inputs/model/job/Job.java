/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.Objects;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Job {

    private final String name;
    private Solver solver;
    private final Modeler modeler;
    private final Simulation simulation;
    private final Outputs outputs;

    public Job(String name, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {
        this(name, new Solver(dynawoParameters.getSolver()), new Modeler(dynawoParameters.getNetwork()), new Simulation(parameters), new Outputs());
    }

    public Job(String name, Solver solver, Modeler modeler, Simulation simulation, Outputs outputs) {
        this.name = Objects.requireNonNull(name);
        this.solver = Objects.requireNonNull(solver);
        this.modeler = Objects.requireNonNull(modeler);
        this.simulation = Objects.requireNonNull(simulation);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public String getName() {
        return name;
    }

    public Solver getSolver() {
        return solver;
    }

    public Modeler getModeler() {
        return modeler;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Outputs getOutputs() {
        return outputs;
    }

}

/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Job {

    private final String name;
    private Solver solver;
    private final Modeler modeler;
    private final Simulation simulation;
    private final Outputs outputs;

    public Job(String name, Solver solver, Modeler modeler, Simulation simulation, Outputs outputs) {
        this.name = Objects.requireNonNull(name);
        // FIXME review. Solver may be null? 
        // Default values could be specified as parameters from command line
        // and applied later to the inputs, before sending data to Dynawo 
        this.solver = solver;
        this.modeler = Objects.requireNonNull(modeler);
        this.simulation = Objects.requireNonNull(simulation);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
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

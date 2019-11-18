/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoJob {

    private final String name;
    private final DynawoSolver solver;
    private final DynawoModeler modeler;
    private final DynawoSimulation simulation;
    private final DynawoOutputs outputs;

    public DynawoJob(String name, DynawoSolver solver, DynawoModeler modeler, DynawoSimulation simulation, DynawoOutputs outputs) {
        this.name = Objects.requireNonNull(name);
        this.solver = Objects.requireNonNull(solver);
        this.modeler = Objects.requireNonNull(modeler);
        this.simulation = Objects.requireNonNull(simulation);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public String getName() {
        return name;
    }

    public DynawoSolver getSolver() {
        return solver;
    }

    public DynawoModeler getModeler() {
        return modeler;
    }

    public DynawoSimulation getSimulation() {
        return simulation;
    }

    public DynawoOutputs getOutputs() {
        return outputs;
    }
}

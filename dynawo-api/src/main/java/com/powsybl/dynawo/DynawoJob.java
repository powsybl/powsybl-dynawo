package com.powsybl.dynawo;

public class DynawoJob {

    private final String name;
    private final DynawoSolver solver;
    private final DynawoModeler modeler;
    private final DynawoSimulation simulation;
    private final DynawoOutputs outputs;

    public DynawoJob(String name, DynawoSolver solver, DynawoModeler modeler, DynawoSimulation simulation,
        DynawoOutputs outputs) {
        this.name = name;
        this.solver = solver;
        this.modeler = modeler;
        this.simulation = simulation;
        this.outputs = outputs;
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

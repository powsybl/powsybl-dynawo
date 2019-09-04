package com.powsybl.dynawo;

public class DynawoCurve {

    private final String model;
    private final String variable;

    public DynawoCurve(String model, String variable) {
        this.model = model;
        this.variable = variable;
    }

    public String getModel() {
        return model;
    }

    public String getVariable() {
        return variable;
    }

}

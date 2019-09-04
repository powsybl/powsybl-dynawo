package com.powsybl.dynawo;

public class DynawoModeler {

    private final String compile;
    private final String iidm;
    private final String parameters;
    private final int parameterId;
    private final String dyd;

    public DynawoModeler(String compile, String iidm, String parameters, int parameterId, String dyd) {
        this.compile = compile;
        this.iidm = iidm;
        this.parameters = parameters;
        this.parameterId = parameterId;
        this.dyd = dyd;
    }

    public String getCompile() {
        return compile;
    }

    public String getIidm() {
        return iidm;
    }

    public String getParameters() {
        return parameters;
    }

    public int getParameterId() {
        return parameterId;
    }

    public String getDyd() {
        return dyd;
    }

}

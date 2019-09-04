package com.powsybl.dynawo;

public class DynawoOutputs {

    private final String directory;
    private final String curve;

    public DynawoOutputs(String directory, String curve) {
        this.directory = directory;
        this.curve = curve;
    }

    public String getDirectory() {
        return directory;
    }

    public String getCurve() {
        return curve;
    }

}

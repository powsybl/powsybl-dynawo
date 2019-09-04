package com.powsybl.dynawo;

public class DynawoSolver {

    private final String lib;
    private final String file;
    private final int id;

    public DynawoSolver(String lib, String file, int id) {
        this.lib = lib;
        this.file = file;
        this.id = id;
    }

    public String getLib() {
        return lib;
    }

    public String getFile() {
        return file;
    }

    public int getId() {
        return id;
    }
}

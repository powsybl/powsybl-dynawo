package com.powsybl.dynawaltz.dynamicmodels.utils;

import java.util.Objects;

public class CoupleLibs {

    private final String lib1;
    private final String lib2;

    public CoupleLibs(String lib1, String lib2) {
        this.lib1 = lib1;
        this.lib2 = lib2;
    }

    public String getLib1() {
        return lib1;
    }

    public String getLib2() {
        return lib2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lib1, lib2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CoupleLibs) {
            CoupleLibs cpl = (CoupleLibs) obj;
            if (cpl.getLib1().equals(lib1) && cpl.getLib2().equals(lib2)
                || cpl.getLib2().equals(lib1) && cpl.getLib1().equals(lib2)) {
                return true;
            }
        }
        return false;
    }
}

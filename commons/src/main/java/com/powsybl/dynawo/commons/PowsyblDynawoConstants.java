package com.powsybl.dynawo.commons;

public final class PowsyblDynawoConstants {

    private PowsyblDynawoConstants() {
    }

    public static final PowsyblDynawoVersion POWSYBL_DYNAWO_VERSION = new PowsyblDynawoVersion();

    public static String getProviderVersion() {
        return POWSYBL_DYNAWO_VERSION.getMavenProjectVersion();
    }
}

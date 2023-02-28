package com.powsybl.dynawaltz.models.utils;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Terminal;

public final class BusUtils {

    private BusUtils() {
    }

    public static String getConnectableBusStaticId(Terminal terminal) {
        return terminal.getBusBreakerView().getConnectableBus().getId();
    }

    public static String getConnectableBusStaticId(Generator generator) {
        return getConnectableBusStaticId(generator.getTerminal());
    }

    public static String getConnectableBusStaticId(Load load) {
        return getConnectableBusStaticId(load.getTerminal());
    }
}

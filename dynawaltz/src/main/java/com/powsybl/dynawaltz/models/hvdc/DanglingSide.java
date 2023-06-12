package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;

import java.util.List;
import java.util.Objects;

public final class DanglingSide {

    private final String prefix;
    private final Side side;

    public DanglingSide(String prefix, Side side) {
        this.prefix = Objects.requireNonNull(prefix);
        this.side = Objects.requireNonNull(side);
    }

    boolean isDangling(Side side) {
        return this.side == side;
    }

    public int getSideNumber() {
        return side.getSideNumber();
    }

    boolean isSide1Dangling() {
        return Side.ONE == side;
    }

    boolean isSide2Dangling() {
        return Side.TWO == side;
    }

    public List<VarConnection> getVarConnectionsWith(BusModel connected) {
        return List.of(new VarConnection(prefix + side.getSideNumber(), connected.getTerminalVarName()));
    }
}

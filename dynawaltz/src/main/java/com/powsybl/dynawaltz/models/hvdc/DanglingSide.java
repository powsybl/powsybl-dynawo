package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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

    public void createMacroConnections(BiFunction<BusModel, Side, List<VarConnection>> basicVarConnectionsSupplier,
                                       BiConsumer<BiFunction<BusModel, Side, List<VarConnection>>, Side> connectionCreator) {
        connectionCreator.accept(this::getVarConnectionsWith, side);
        connectionCreator.accept(basicVarConnectionsSupplier, side.getOppositeSide());
    }

    private List<VarConnection> getVarConnectionsWith(BusModel connected, Side side) {
        return List.of(new VarConnection(prefix + side.getSideNumber(), connected.getTerminalVarName()));
    }
}

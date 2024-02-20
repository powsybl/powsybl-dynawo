package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class DanglingSide {

    private final String prefix;
    private final TwoSides side;

    public DanglingSide(String prefix, TwoSides side) {
        this.prefix = Objects.requireNonNull(prefix);
        this.side = Objects.requireNonNull(side);
    }

    boolean isDangling(TwoSides side) {
        return this.side == side;
    }

    public int getSideNumber() {
        return side.getNum();
    }

    public void createMacroConnections(BiFunction<EquipmentConnectionPoint, TwoSides, List<VarConnection>> basicVarConnectionsSupplier,
                                       BiConsumer<BiFunction<EquipmentConnectionPoint, TwoSides, List<VarConnection>>, TwoSides> connectionCreator) {
        connectionCreator.accept(this::getVarConnectionsWith, side);
        connectionCreator.accept(basicVarConnectionsSupplier, SideUtils.getOppositeSide(side));
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        return List.of(new VarConnection(prefix + side.getNum(), connected.getTerminalVarName(side)));
    }
}

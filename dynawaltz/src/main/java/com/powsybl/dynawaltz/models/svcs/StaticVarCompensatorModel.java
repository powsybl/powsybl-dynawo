package com.powsybl.dynawaltz.models.svcs;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.StaticVarCompensator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StaticVarCompensatorModel extends AbstractEquipmentBlackBoxModel<StaticVarCompensator> {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("SVarC_injector_PInjPu", "p"),
            new VarMapping("SVarC_injector_QInjPu", "q"),
            new VarMapping("SVarC_injector_state", "state"),
            new VarMapping("SVarC_modeHandling_mode_value", "regulatingMode"));

    private final String compensatorLib;

    public StaticVarCompensatorModel(String dynamicModelId, StaticVarCompensator svc, String parameterSetId, String compensatorLib) {
        super(dynamicModelId, parameterSetId, svc);
        this.compensatorLib = Objects.requireNonNull(compensatorLib);
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(BusUtils.getConnectableBusStaticId(equipment), BusModel.class, this::getVarConnectionsWithBus, context);
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return List.of(new VarConnection("SVarC_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public String getLib() {
        return compensatorLib;
    }
}

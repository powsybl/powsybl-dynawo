package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.LineSideUtils;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Line;

import java.util.*;

public class StandardLine extends AbstractBlackBoxModel implements LineModel {

    private final Map<String, Branch.Side> busSideConnection = new HashMap<>();

    public StandardLine(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "Line";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (connected instanceof BusModel) {
            BusModel busModel = (BusModel) connected;
            return Arrays.asList(
                    new VarConnection(getIVarName(busSideConnection.get(busModel.getStaticId().orElseThrow())), busModel.getNumCCVarName()),
                    new VarConnection(getStateVarName(), busModel.getTerminalVarName())
            );
        } else {
            throw new PowsyblException("StandardLineModel can only connect to BusModel");
        }
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        String staticId = getStaticId().orElse(null);
        Line line = context.getNetwork().getLine(staticId);
        if (line == null) {
            throw new PowsyblException("Line static id unknown: " + staticId);
        }
        List<Model> connectedBbm = new ArrayList<>(2);
        line.getTerminals().forEach(t -> {
            BusModel busModel = context.getDynamicModelOrDefaultBus(BusUtils.getConnectableBusStaticId(t));
            busSideConnection.put(busModel.getStaticId().orElseThrow(), line.getSide(t));
            connectedBbm.add(busModel);
        });
        return connectedBbm;
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public String getIVarName(Branch.Side side) {
        return getDynamicModelId() + LineSideUtils.getSuffix(side);
    }

    @Override
    public String getStateVarName() {
        return getDynamicModelId() + "_state";
    }

    @Override
    public String getDesactivateCurrentLimitsVarName() {
        return getDynamicModelId() + "_desactivate_currentLimits";
    }

    @Override
    public String getStateValueVarName() {
        return getDynamicModelId() + "_state_value";
    }
}

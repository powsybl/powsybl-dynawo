package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardLine extends AbstractBlackBoxModel implements LineModel {

    private final String sidePostfix;

    public StandardLine(String dynamicModelId, String staticId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, staticId, parameterSetId);
        this.sidePostfix = LineModel.getSuffix(side);
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
                    new VarConnection(getIVarName(), busModel.getNumCCVarName()),
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
            throw new PowsyblException("Line static id unknown: " + getStaticId());
        }
        List<Model> connectedBbm = new ArrayList<>(2);
        line.getTerminals().forEach(t -> connectedBbm.add(context.getDynamicModelOrDefaultBus(BusUtils.getConnectableBusStaticId(t))));
        return connectedBbm;
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public String getIVarName() {
        return getDynamicModelId() + sidePostfix;
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

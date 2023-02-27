package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardLine extends AbstractBlackBoxModel implements LineModel {

    private final String sidePostfix;

    public StandardLine(String dynamicModelId, String statidId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, statidId, parameterSetId);
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
    public List<Model> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext) {
        String staticId = getStaticId().orElse(null);
        Line line = dynaWaltzContext.getNetwork().getLine(staticId);
        if (line == null) {
            throw new PowsyblException("Line static id unkwown: " + staticId);
        }
        List<Model> connectedBbm = new ArrayList<>();
        for (Bus b : dynaWaltzContext.getNetwork().getBusBreakerView().getBuses()) {
            if (b.getLineStream().anyMatch(l -> l.equals(line))) {
                connectedBbm.add(dynaWaltzContext.getStaticIdBlackBoxModel(b.getId()));
            }
        }
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

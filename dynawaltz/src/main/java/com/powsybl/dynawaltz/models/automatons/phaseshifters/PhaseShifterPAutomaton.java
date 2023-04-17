package com.powsybl.dynawaltz.models.automatons.phaseshifters;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

public class PhaseShifterPAutomaton extends AbstractPhaseShifterAutomaton {

    public PhaseShifterPAutomaton(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId) {
        super(dynamicModelId, transformer, parameterSetId);
    }

    @Override
    public String getLib() {
        return "PhaseShifterP";
    }

    protected List<VarConnection> getVarConnectionsWithTransformer(TransformerModel connected) {
        return Arrays.asList(
                new VarConnection("phaseShifter_tap", connected.getStepVarName()),
                new VarConnection("phaseShifter_PMonitored", connected.getPMonitoredVarName()),
                new VarConnection("phaseShifter_AutomatonExists", connected.getDisableInternalTapChangerVarName())
        );
    }
}

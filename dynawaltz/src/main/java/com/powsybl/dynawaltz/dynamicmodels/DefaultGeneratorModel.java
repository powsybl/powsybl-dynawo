package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.commons.PowsyblException;

public class DefaultGeneratorModel extends AbstractNetworkBlackBoxModel implements GeneratorModel {

    public DefaultGeneratorModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getLib() {
        return "NetworkGenerator";
    }

    @Override
    public String getTerminalVarName() {
        return "@NAME@_terminal";
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return "@NAME@_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "@NAME@_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "@NAME@_switchOffSignal3";
    }

    @Override
    public String getOmegaPuVarName() {
        throw new PowsyblException("NetworkGenerator does not have 'omegaPu' variable");
    }

    @Override
    public String getOmegaRefPuVarName() {
        throw new PowsyblException("NetworkGenerator does not have 'omegaRefPu' variable");
    }

    @Override
    public String getRunningVarName() {
        throw new PowsyblException("NetworkGenerator does not have 'running' variable");
    }
}

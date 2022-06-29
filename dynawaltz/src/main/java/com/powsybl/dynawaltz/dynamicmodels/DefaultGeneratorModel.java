package com.powsybl.dynawaltz.dynamicmodels;

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
        return null; //TODO
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return null; //TODO
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return null; //TODO
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return null; //TODO
    }

    @Override
    public String getOmegaPuVarName() {
        return null; //TODO
    }

    @Override
    public String getOmegaRefPuVarName() {
        return null; //TODO
    }

    @Override
    public String getRunningVarName() {
        return null; //TODO
    }
}

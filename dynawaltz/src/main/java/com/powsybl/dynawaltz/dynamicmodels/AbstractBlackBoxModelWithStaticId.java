package com.powsybl.dynawaltz.dynamicmodels;

public abstract class AbstractBlackBoxModelWithStaticId extends AbstractBlackBoxModel implements BlackBoxModelWithStaticId {

    private final String staticId;

    protected AbstractBlackBoxModelWithStaticId(String dynamicModelId, String staticId, String parametersId) {
        super(dynamicModelId, parametersId);
        this.staticId = staticId;
    }

    @Override
    public String getStaticId() {
        return staticId;
    }
}

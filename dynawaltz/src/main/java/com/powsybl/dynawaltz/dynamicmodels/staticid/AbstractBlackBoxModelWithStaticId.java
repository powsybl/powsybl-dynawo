package com.powsybl.dynawaltz.dynamicmodels.staticid;

import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModelWithDynamicId;

public abstract class AbstractBlackBoxModelWithStaticId extends AbstractBlackBoxModelWithDynamicId implements BlackBoxModelWithStaticId {

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

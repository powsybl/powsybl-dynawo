package com.powsybl.dynawaltz.dynamicmodels.staticid;

import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModelExtended;

public abstract class AbstractBlackBoxModelWithStaticId extends AbstractBlackBoxModelExtended implements BlackBoxModelWithStaticId {

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

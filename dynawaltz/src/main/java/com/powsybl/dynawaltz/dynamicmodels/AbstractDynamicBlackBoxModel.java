package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynamicsimulation.DynamicModel;

public abstract class AbstractDynamicBlackBoxModel extends AbstractBlackBoxModel implements DynamicModel {
    protected AbstractDynamicBlackBoxModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }
}

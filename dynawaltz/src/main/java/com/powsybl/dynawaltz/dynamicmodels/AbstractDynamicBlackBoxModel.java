package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynamicsimulation.DynamicModel;

/**
 * This abstract class is used when instantiating DynamicModel objects, described according to the DSL norm applied to DynaWaltz.
 */
public abstract class AbstractDynamicBlackBoxModel extends AbstractBlackBoxModel implements DynamicModel {
    protected AbstractDynamicBlackBoxModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }
}

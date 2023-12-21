package com.powsybl.dynawaltz.builders;

import java.util.List;

public class ModelCategory {
//TODO paser en record si on garde ce model
    public String category;
    public DynamicModelBuilderUtils.ModelBuilderConstructorFull builderConstructor;
    public List<ModelConfig> modelConfigs;

    public ModelCategory(String category, DynamicModelBuilderUtils.ModelBuilderConstructorFull builderConstructor, List<ModelConfig> modelConfigs) {
        this.category = category;
        this.builderConstructor = builderConstructor;
        this.modelConfigs = modelConfigs;
    }
}

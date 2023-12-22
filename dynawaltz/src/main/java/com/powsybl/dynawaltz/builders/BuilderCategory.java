package com.powsybl.dynawaltz.builders;

public interface BuilderCategory {

    String getCategoryName();

    DynamicModelBuilderUtils.ModelBuilderConstructor getConstructor();
}

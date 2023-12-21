package com.powsybl.dynawaltz.builders;

public class EventModelCategory {
//TODO paser en record si on garde ce model
    public String tag;
    public DynamicModelBuilderUtils.EventModelBuilderConstructor builderConstructor;

    public EventModelCategory(String tag, DynamicModelBuilderUtils.EventModelBuilderConstructor builderConstructor) {
        this.tag = tag;
        this.builderConstructor = builderConstructor;
    }
}

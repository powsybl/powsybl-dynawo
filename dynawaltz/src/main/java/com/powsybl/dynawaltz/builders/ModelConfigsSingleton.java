package com.powsybl.dynawaltz.builders;

import java.util.*;

public final class ModelConfigsSingleton {

    private static ModelConfigsSingleton INSTANCE;

    private final Map<String, List<ModelConfig>> modelConfigs = new HashMap<>();
    private final List<DynamicModelCategory> dynamicModelCategories = new ArrayList<>();

    private ModelConfigsSingleton() {
        for (ModelConfigLoader configLoader : ServiceLoader.load(ModelConfigLoader.class)) {
            configLoader.loadModelConfigs().forEach(
                    (cat, models) -> modelConfigs.merge(cat, models, (v1, v2) -> {
                        v1.addAll(v2);
                        return v1;
                    })
            );
            configLoader.loadBuilderCategories().forEach(bc ->
                dynamicModelCategories.add(new DynamicModelCategory(bc.getCategoryName(),
                        bc.getConstructor(), modelConfigs.get(bc.getCategoryName()))));
        }
    }

    //TODO change singleton type ?
    public static ModelConfigsSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModelConfigsSingleton();
        }
        return INSTANCE;
    }

    public Map<String, List<ModelConfig>> getModelConfigs() {
        return modelConfigs;
    }

    public List<DynamicModelCategory> getDynamicModelCategories() {
        return dynamicModelCategories;
    }
}

/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.common.collect.Lists;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.events.NodeFaultEventBuilder;
import com.powsybl.iidm.network.Network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigs {

    private static final ModelConfigs INSTANCE = new ModelConfigs();

    private final List<ModelConfigLoader> modelConfigLoaders;
    private final Map<String, Map<String, ModelConfig>> modelConfigsMap = new HashMap<>();
    private final List<BuilderConfig> builderConfigs;
    //TODO remove ?
    private final Map<String, BuilderConfig.ModelBuilderConstructor> builderConstructorByCategoryName;
    private final Map<String, BuilderConfig.ModelBuilderConstructor> builderConstructorByName = new HashMap<>();

    private final List<EventBuilderConfig> eventBuilderConfigs = List.of(
            new EventBuilderConfig(EventActivePowerVariationBuilder::of, EventActivePowerVariationBuilder.TAG),
            new EventBuilderConfig(EventDisconnectionBuilder::of, EventDisconnectionBuilder.TAG),
            new EventBuilderConfig(NodeFaultEventBuilder::of, NodeFaultEventBuilder.TAG));

    private ModelConfigs() {
        modelConfigLoaders = Lists.newArrayList(ServiceLoader.load(ModelConfigLoader.class));
        modelConfigLoaders.forEach(l -> l.loadModelConfigs().forEach(
                (cat, modelsMap) -> modelConfigsMap.merge(cat, modelsMap, (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                })
        ));
        builderConfigs = modelConfigLoaders.stream().flatMap(ModelConfigLoader::loadBuilderConfigs).toList();
        builderConstructorByCategoryName = builderConfigs.stream().collect(Collectors.toMap(BuilderConfig::getCategory, BuilderConfig::getBuilderConstructor));
        builderConfigs.forEach(bc -> modelConfigsMap.get(bc.getCategory()).keySet()
                .forEach(lib -> builderConstructorByName.put(lib, bc.getBuilderConstructor())));
    }

    public static ModelConfigs getInstance() {
        return INSTANCE;
    }

    public Map<String, ModelConfig> getModelConfigs(String categoryName) {
        return modelConfigsMap.get(categoryName);
    }

    public List<BuilderConfig> getBuilderConfigs() {
        return builderConfigs;
    }

    public List<EventBuilderConfig> getEventBuilderConfigs() {
        return eventBuilderConfigs;
    }

    public BuilderConfig.ModelBuilderConstructor getModelBuilderConstructorFromCategory(String modelType) {
        return builderConstructorByCategoryName.get(modelType);
    }

    public ModelBuilder<DynamicModel> getModelBuilder(Network network, String modelName, Reporter reporter) {
        return builderConstructorByName.get(modelName).createBuilder(network, modelName, reporter);
    }
}

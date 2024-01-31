/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.common.collect.Lists;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.events.NodeFaultEventBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigsHandler {

    private static final ModelConfigsHandler INSTANCE = new ModelConfigsHandler();

    private final Map<String, ModelConfigs> modelConfigsCat = new HashMap<>();

    private final List<BuilderConfig> builderConfigs;
    private final List<EventBuilderConfig> eventBuilderConfigs = List.of(
            new EventBuilderConfig(EventActivePowerVariationBuilder::of, EventActivePowerVariationBuilder.TAG),
            new EventBuilderConfig(EventDisconnectionBuilder::of, EventDisconnectionBuilder.TAG),
            new EventBuilderConfig(NodeFaultEventBuilder::of, NodeFaultEventBuilder.TAG));

    private ModelConfigsHandler() {
        List<ModelConfigLoader> modelConfigLoaders = Lists.newArrayList(ServiceLoader.load(ModelConfigLoader.class));
        modelConfigLoaders.forEach(l -> l.loadModelConfigs().forEach(
                (cat, modelsMap) -> modelConfigsCat.merge(cat, modelsMap, (configs1, configs2) -> {
                    configs1.addModelConfigs(configs2);
                    return configs1;
                })
        ));
        builderConfigs = modelConfigLoaders.stream().flatMap(ModelConfigLoader::loadBuilderConfigs).toList();
    }

    public static ModelConfigsHandler getInstance() {
        return INSTANCE;
    }

    public ModelConfigs getModelConfigsNew(String categoryName) {
        return modelConfigsCat.get(categoryName);
    }

    public List<BuilderConfig> getBuilderConfigs() {
        return builderConfigs;
    }

    public List<EventBuilderConfig> getEventBuilderConfigs() {
        return eventBuilderConfigs;
    }
}

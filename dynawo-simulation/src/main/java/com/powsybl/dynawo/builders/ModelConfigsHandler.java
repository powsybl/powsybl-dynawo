/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.google.common.collect.Lists;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.iidm.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConfigsHandler.class);
    private static final ModelConfigsHandler INSTANCE = new ModelConfigsHandler();

    private final Map<String, ModelConfigs> modelConfigsCat = new HashMap<>();
    private final List<BuilderConfig> builderConfigs;
    private final Map<String, BuilderConfig.ModelBuilderConstructor> builderConstructorByName = new HashMap<>();
    private final List<EventBuilderConfig> eventBuilderConfigs;
    private final Map<String, EventBuilderConfig.EventModelBuilderConstructor> eventBuilderConstructorByName;

    private ModelConfigsHandler() {
        List<ModelConfigLoader> modelConfigLoaders = Lists.newArrayList(ServiceLoader.load(ModelConfigLoader.class));
        modelConfigLoaders.forEach(l -> l.loadModelConfigs().forEach(
                (cat, modelsMap) -> modelConfigsCat.merge(cat, modelsMap, (configs1, configs2) -> {
                    configs1.addModelConfigs(configs2);
                    return configs1;
                })
        ));
        builderConfigs = modelConfigLoaders.stream()
                .flatMap(ModelConfigLoader::loadBuilderConfigs)
                .sorted(Comparator.comparing(BuilderConfig::getCategory))
                .toList();
        builderConfigs.forEach(bc -> modelConfigsCat.get(bc.getCategory()).getModelsName()
                .forEach(lib -> builderConstructorByName.put(lib, bc.getBuilderConstructor())));
        eventBuilderConfigs = modelConfigLoaders.stream()
                .flatMap(ModelConfigLoader::loadEventBuilderConfigs)
                .sorted(Comparator.comparing(e -> e.getEventModelInfo().name()))
                .toList();
        eventBuilderConstructorByName = eventBuilderConfigs.stream()
                .collect(Collectors.toMap(e -> e.getEventModelInfo().name(), EventBuilderConfig::getBuilderConstructor));
    }

    public static ModelConfigsHandler getInstance() {
        return INSTANCE;
    }

    public ModelConfigs getModelConfigs(String categoryName) {
        return modelConfigsCat.get(categoryName);
    }

    public List<BuilderConfig> getBuilderConfigs() {
        return builderConfigs;
    }

    public List<EventBuilderConfig> getEventBuilderConfigs() {
        return eventBuilderConfigs;
    }

    public ModelBuilder<DynamicModel> getModelBuilder(Network network, String modelName, ReportNode reportNode) {
        BuilderConfig.ModelBuilderConstructor constructor = builderConstructorByName.get(modelName);
        if (constructor == null) {
            BuilderReports.reportBuilderNotFound(reportNode, modelName);
            return null;
        }
        return constructor.createBuilder(network, modelName, reportNode);
    }

    public ModelBuilder<EventModel> getEventModelBuilder(Network network, String modelName, ReportNode reportNode) {
        EventBuilderConfig.EventModelBuilderConstructor constructor = eventBuilderConstructorByName.get(modelName);
        if (constructor == null) {
            BuilderReports.reportBuilderNotFound(reportNode, modelName);
            return null;
        }
        return constructor.createBuilder(network, reportNode);
    }

    public void addModels(AdditionalModelConfigLoader additionalModelsLoader) {
        additionalModelsLoader.loadModelConfigs().forEach(
                (cat, modelsMap) -> {
                    ModelConfigs currentModelConfigs = modelConfigsCat.get(cat);
                    if (currentModelConfigs != null) {
                        currentModelConfigs.addModelConfigs(modelsMap);
                        BuilderConfig.ModelBuilderConstructor constructor = builderConfigs.stream()
                                    .filter(bc -> bc.getCategory().equals(cat))
                                    .map(BuilderConfig::getBuilderConstructor)
                                    .findFirst()
                                    .orElse(null);
                        modelsMap.getModelsName().forEach(lib -> builderConstructorByName.put(lib, constructor));
                    } else {
                        LOGGER.warn("Category {} not found, the additional models under this category will be skipped", cat);
                    }
                }
        );

    }
}

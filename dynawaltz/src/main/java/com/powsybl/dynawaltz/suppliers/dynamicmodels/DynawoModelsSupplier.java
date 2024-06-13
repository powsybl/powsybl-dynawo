/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.builders.ModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.suppliers.Property;
import com.powsybl.dynawaltz.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Instantiates an {@link DynamicModelConfig} list from {@link DynamicModelConfig} or JSON input
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoModelsSupplier implements DynamicModelsSupplier {

    private static final String PARAMETER_ID_FIELD = "parameterSetId";

    private final List<DynamicModelConfig> dynamicModelConfigs;

    public DynawoModelsSupplier(List<DynamicModelConfig> dynamicModelConfigs) {
        this.dynamicModelConfigs = dynamicModelConfigs;
    }

    public DynawoModelsSupplier(InputStream is) {
        this.dynamicModelConfigs = new SupplierJsonDeserializer<>(new DynamicModelConfigsJsonDeserializer()).deserialize(is);
    }

    public DynawoModelsSupplier(Path path) {
        this.dynamicModelConfigs = new SupplierJsonDeserializer<>(new DynamicModelConfigsJsonDeserializer()).deserialize(path);
    }

    @Override
    public String getName() {
        return DynaWaltzProvider.NAME;
    }

    @Override
    public List<DynamicModel> get(Network network, ReportNode reportNode) {
        return dynamicModelConfigs.stream()
                .map(dynamicModelConfig -> buildDynamicModel(dynamicModelConfig, network, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    private static DynamicModel buildDynamicModel(DynamicModelConfig dynamicModelConfig, Network network, ReportNode reportNode) {
        ModelBuilder<DynamicModel> builder = ModelConfigsHandler.getInstance().getModelBuilder(network, dynamicModelConfig.model(), reportNode);
        if (builder != null) {
            Class<? extends ModelBuilder> builderClass = builder.getClass();
            invokeParameterIdMethod(builderClass, builder, getParameterSetId(dynamicModelConfig));
            dynamicModelConfig.properties().forEach(p -> invokeMethod(builderClass, builder, p));
            return builder.build();
        }
        return null;
    }

    private static void invokeMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<DynamicModel> builder, Property property) {
        try {
            builderClass.getMethod(property.name(), property.propertyClass()).invoke(builder, property.value());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", property.name(), property.value(), builderClass.getSimpleName()), e);
        }
    }

    private static void invokeParameterIdMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<DynamicModel> builder, String parameterSetId) {
        try {
            builderClass.getMethod(DynawoModelsSupplier.PARAMETER_ID_FIELD, String.class).invoke(builder, parameterSetId);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", DynawoModelsSupplier.PARAMETER_ID_FIELD, parameterSetId, builderClass.getSimpleName()), e);
        }
    }

    private static String getParameterSetId(DynamicModelConfig config) {
        return switch (config.groupType()) {
            case FIXED -> config.group();
            case PREFIX -> config.group() + getDynamicModelIdProperty(config.properties());
            case SUFFIX -> getDynamicModelIdProperty(config.properties()) + config.group();
        };
    }

    private static String getDynamicModelIdProperty(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.name().equalsIgnoreCase("dynamicModelId"))
                .map(p -> (String) p.value())
                .findFirst()
                .orElseGet(() -> getStaticIdProperty(properties));
    }

    private static String getStaticIdProperty(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.name().equalsIgnoreCase("staticId"))
                .map(p -> (String) p.value())
                .findFirst()
                .orElseThrow(() -> new PowsyblException("No ID found for parameter set id"));
    }
}

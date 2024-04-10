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
import com.powsybl.dynawaltz.builders.ModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.suppliers.Property;
import com.powsybl.iidm.network.Network;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoDynamicModelSupplier implements DynamicModelsSupplier {

    private static final String PARAMETER_ID_FIELD = "parameterSetId";

    private final List<DynamicModelConfig> dynamicModelConfigs;

    public DynawoDynamicModelSupplier(List<DynamicModelConfig> dynamicModelConfigs) {
        this.dynamicModelConfigs = dynamicModelConfigs;
    }

    @Override
    public List<DynamicModel> get(Network network, ReportNode reportNode) {
        return dynamicModelConfigs.stream()
                .map(dynamicModelConfig -> buildDynamicModel(dynamicModelConfig, network, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    private static DynamicModel buildDynamicModel(DynamicModelConfig dynamicModelConfig, Network network, ReportNode reportNode) {
        ModelBuilder<DynamicModel> builder = ModelConfigsHandler.getInstance().getModelBuilder(network, dynamicModelConfig.getModel(), reportNode);
        if (builder != null) {
            Class<? extends ModelBuilder> builderClass = builder.getClass();
            invokeParameterIdMethod(builderClass, builder, dynamicModelConfig.getGroup());
            dynamicModelConfig.getProperties().forEach(p -> invokeMethod(builderClass, builder, p));
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

    private static void invokeParameterIdMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<DynamicModel> builder, String value) {
        try {
            builderClass.getMethod(DynawoDynamicModelSupplier.PARAMETER_ID_FIELD, String.class).invoke(builder, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", DynawoDynamicModelSupplier.PARAMETER_ID_FIELD, value, builderClass.getSimpleName()), e);
        }
    }
}

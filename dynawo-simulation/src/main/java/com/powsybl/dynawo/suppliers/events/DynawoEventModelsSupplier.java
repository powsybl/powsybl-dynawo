/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.ModelBuilder;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Instantiates an {@link EventModel} list from {@link EventModelConfig} or JSON input
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoEventModelsSupplier implements EventModelsSupplier {

    private final List<EventModelConfig> eventModelConfigs;

    public static DynawoEventModelsSupplier load(InputStream is) {
        return new DynawoEventModelsSupplier(new SupplierJsonDeserializer<>(new EventModelConfigsJsonDeserializer()).deserialize(is));
    }

    public static DynawoEventModelsSupplier load(Path path) {
        return new DynawoEventModelsSupplier(new SupplierJsonDeserializer<>(new EventModelConfigsJsonDeserializer()).deserialize(path));
    }

    public DynawoEventModelsSupplier(List<EventModelConfig> eventModelConfigs) {
        this.eventModelConfigs = eventModelConfigs;
    }

    @Override
    public String getName() {
        return DynawoSimulationProvider.NAME;
    }

    @Override
    public List<EventModel> get(Network network, ReportNode reportNode) {
        ReportNode supplierReportNode = reportNode.newReportNode()
                .withMessageTemplate("jsonEventModels", "Dynawo Event Models Supplier")
                .add();
        return eventModelConfigs.stream()
                .map(eventModelConfig -> buildEventModel(eventModelConfig, network, supplierReportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    private static EventModel buildEventModel(EventModelConfig eventModelConfig, Network network, ReportNode reportNode) {
        ModelBuilder<EventModel> builder = ModelConfigsHandler.getInstance().getEventModelBuilder(network, eventModelConfig.model(), reportNode);
        if (builder != null) {
            Class<? extends ModelBuilder> builderClass = builder.getClass();
            eventModelConfig.properties().forEach(p -> invokeMethod(builderClass, builder, p));
            return builder.build();
        }
        return null;
    }

    private static void invokeMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<EventModel> builder, Property property) {
        try {
            builderClass.getMethod(property.name(), property.propertyClass()).invoke(builder, property.value());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", property.name(), property.value(), builderClass.getSimpleName()), e);
        }
    }
}

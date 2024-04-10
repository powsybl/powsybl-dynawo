/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
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
public class DynawoEventModelsSupplier implements EventModelsSupplier {

    private final List<EventModelConfig> eventModelConfigs;

    public DynawoEventModelsSupplier(List<EventModelConfig> eventModelConfigs) {
        this.eventModelConfigs = eventModelConfigs;
    }

    @Override
    public List<EventModel> get(Network network, ReportNode reportNode) {
        return eventModelConfigs.stream()
                .map(eventModelConfig -> buildEventModel(eventModelConfig, network, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    private static EventModel buildEventModel(EventModelConfig eventModelConfig, Network network, ReportNode reportNode) {
        ModelBuilder<EventModel> builder = ModelConfigsHandler.getInstance().getEventModelBuilder(network, eventModelConfig.getModel(), reportNode);
        if (builder != null) {
            Class<? extends ModelBuilder> builderClass = builder.getClass();
            eventModelConfig.getProperties().forEach(p -> invokeMethod(builderClass, builder, p));
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

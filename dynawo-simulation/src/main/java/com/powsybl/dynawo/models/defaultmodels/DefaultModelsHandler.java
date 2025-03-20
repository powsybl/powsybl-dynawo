/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.defaultmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.models.Model;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DefaultModelsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModelsHandler.class);

    private static final Map<IdentifiableType, List<DefaultModelConfiguration>> CONFIGURATIONS = EnumSet.allOf(DefaultModelConfiguration.class)
            .stream()
            .collect(groupingBy(DefaultModelConfiguration::getIdentifiableType));

    public <T extends Model> T getDefaultModel(Identifiable<?> equipment, Class<T> connectableClass, boolean throwException) {
        DefaultModelConfiguration conf = findConfiguration(equipment.getType(), connectableClass);
        Model defaultModel = conf.getDefaultModel(equipment.getId());
        if (connectableClass.isInstance(defaultModel)) {
            return connectableClass.cast(defaultModel);
        }
        if (throwException) {
            throw new PowsyblException("Default model " + defaultModel.getClass().getSimpleName() + " for " + equipment.getId() + " does not implement " + connectableClass.getSimpleName() + " interface");
        } else {
            LOGGER.warn("Default model {} for {} does not implement {} interface", defaultModel.getClass().getSimpleName(), equipment.getId(), connectableClass.getSimpleName());
            return null;
        }
    }

    private <T extends Model> DefaultModelConfiguration findConfiguration(IdentifiableType type, Class<T> connectableClass) {
        List<DefaultModelConfiguration> configurationList = CONFIGURATIONS.get(type);
        if (configurationList == null) {
            throw new PowsyblException("No default model configuration for " + type);
        }
        if (configurationList.size() == 1) {
            return configurationList.get(0);
        } else {
            for (DefaultModelConfiguration configuration : configurationList) {
                if (connectableClass.isAssignableFrom(configuration.getEquipmentClass())) {
                    return configuration;
                }
            }
            throw new PowsyblException("No default model configuration for " + type + " - " + connectableClass.getSimpleName());
        }
    }
}

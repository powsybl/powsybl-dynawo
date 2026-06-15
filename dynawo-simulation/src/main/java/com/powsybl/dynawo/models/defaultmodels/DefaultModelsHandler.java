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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DefaultModelsHandler {

    private static final Map<IdentifiableType, List<DefaultModelConfiguration>> CONFIGURATIONS = EnumSet.allOf(DefaultModelConfiguration.class)
            .stream()
            .collect(groupingBy(DefaultModelConfiguration::getIdentifiableType));

    public <T extends Model> Model getDefaultModel(Identifiable<?> equipment, Class<T> connectableClass) {
        IdentifiableType type = equipment.getType();
        List<DefaultModelConfiguration> configurationList = CONFIGURATIONS.get(type);
        if (configurationList == null) {
            throw new PowsyblException("No default model configuration for " + type);
        }
        if (configurationList.size() == 1) {
            return configurationList.getFirst().getDefaultModel(equipment.getId());
        } else {
            for (DefaultModelConfiguration configuration : configurationList) {
                if (connectableClass.isAssignableFrom(configuration.getEquipmentClass())) {
                    return configuration.getDefaultModel(equipment.getId());
                }
            }
            throw new PowsyblException("No default model configuration for " + type + " - " + connectableClass.getSimpleName());
        }
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.GridFormingConverter
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GridFormingConverterGroovyExtension extends AbstractEquipmentGroovyExtension {

    private static final String GRID_FORMING_CONVERTER = "gridFormingConverter"

    GridFormingConverterGroovyExtension() {
        super(GRID_FORMING_CONVERTER)
    }

    @Override
    protected GridFormingConverterBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new GridFormingConverterBuilder(network, equipmentConfig, reporter)
    }

    static class GridFormingConverterBuilder extends AbstractGeneratorBuilder {

        GridFormingConverterBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        @Override
        GridFormingConverter build() {
            isInstantiable() ? new GridFormingConverter(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                    : null
        }
    }
}

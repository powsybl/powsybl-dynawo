/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformers
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadTwoTransformersGroovyExtension extends AbstractSimpleEquipmentGroovyExtension {

    LoadTwoTransformersGroovyExtension() {
        super("LoadTwoTransformers")
    }

    @Override
    protected LoadTwoTransformersBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new LoadTwoTransformersBuilder(network, equipmentConfig, reporter)
    }

    static class LoadTwoTransformersBuilder extends AbstractLoadModelBuilder {

        LoadTwoTransformersBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        @Override
        LoadTwoTransformers build() {
            isInstantiable() ? new LoadTwoTransformers(dynamicModelId, equipment, parameterSetId) : null
        }
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.models.buses.InfiniteBus
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class InfiniteBusGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String BUSES = "infiniteBuses"

    InfiniteBusGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(BUSES).keySet() as List
    }

    @Override
    protected BusBuilder createBuilder(Network network, String currentTag) {
        new BusBuilder(network, currentTag)
    }

    static class BusBuilder extends AbstractBusBuilder {

        String tag

        BusBuilder(Network network, String tag) {
            super(network)
            this.tag = tag
        }

        @Override
        InfiniteBus build() {
            checkData()
            new InfiniteBus(dynamicModelId, bus, parameterSetId, tag)
        }
    }
}

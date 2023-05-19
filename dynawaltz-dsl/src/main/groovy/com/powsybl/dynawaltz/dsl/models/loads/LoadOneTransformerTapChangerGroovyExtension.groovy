/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerTapChangerGroovyExtension extends AbstractSimpleEquipmentGroovyExtension<LoadOneTransformerTapChanger> {

    LoadOneTransformerTapChangerGroovyExtension() {
        modelTag = "LoadOneTransformerTapChanger"
    }

    @Override
    protected LoadOneTransformerTapChangerBuilder createBuilder(Network network) {
        new LoadOneTransformerTapChangerBuilder(network)
    }

    static class LoadOneTransformerTapChangerBuilder extends AbstractLoadModelBuilder {

        LoadOneTransformerTapChangerBuilder(Network network) {
            super(network)
        }

        @Override
        LoadOneTransformerTapChanger build() {
            checkData()
            new LoadOneTransformerTapChanger(dynamicModelId, load, parameterSetId)
        }
    }
}

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
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadTwoTransformersTapChangersGroovyExtension extends AbstractSimpleEquipmentGroovyExtension<LoadTwoTransformersTapChangers> {

    LoadTwoTransformersTapChangersGroovyExtension() {
        modelTag = "LoadTwoTransformersTapChangers"
    }

    @Override
    protected LoadTwoTransformersTapChangersBuilder createBuilder(Network network) {
        new LoadTwoTransformersTapChangersBuilder(network)
    }

    static class LoadTwoTransformersTapChangersBuilder extends AbstractLoadModelBuilder {

        LoadTwoTransformersTapChangersBuilder(Network network) {
            super(network)
        }

        @Override
        LoadTwoTransformersTapChangers build() {
            checkData()
            new LoadTwoTransformersTapChangers(dynamicModelId, load, parameterSetId)
        }
    }
}

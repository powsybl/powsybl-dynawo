/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.models.buses.StandardBus
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Bus</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class BusGroovyExtension extends AbstractSimpleEquipmentGroovyExtension<StandardBus> {

    BusGroovyExtension() {
        modelTag = "Bus"
    }

    @Override
    protected BusBuilder createBuilder(Network network) {
        new BusBuilder(network)
    }

    static class BusBuilder extends AbstractBusBuilder {

        BusBuilder(Network network) {
            super(network)
        }

        @Override
        StandardBus build() {
            checkData()
            new StandardBus(dynamicModelId, bus, parameterSetId)
        }
    }
}

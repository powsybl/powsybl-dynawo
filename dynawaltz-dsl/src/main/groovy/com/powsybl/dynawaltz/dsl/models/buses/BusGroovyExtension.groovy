/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.buses.StandardBus
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Bus</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class BusGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    BusGroovyExtension() {
        modelTags = ["Bus"]
    }

    @Override
    protected BusBuilder createBuilder(Network network, String currentTag) {
        new BusBuilder(network)
    }

    static class BusBuilder extends AbstractDynamicModelBuilder {

        Bus bus

        BusBuilder(Network network) {
            super(network)
        }

        void checkData() {
            super.checkData()
            bus = network.getBusBreakerView().getBus(staticId)
            if (bus == null) {
                throw new DslException("Bus static id unknown: " + staticId)
            }
        }

        @Override
        StandardBus build() {
            checkData()
            new StandardBus(dynamicModelId, bus, parameterSetId)
        }
    }
}

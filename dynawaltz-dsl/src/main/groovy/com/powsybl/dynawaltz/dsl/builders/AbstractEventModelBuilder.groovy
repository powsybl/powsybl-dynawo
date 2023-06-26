/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractEventModelBuilder<T extends Identifiable> extends AbstractDynamicModelBuilder implements ModelBuilder<EventModel> {

    protected final DslEquipment<T> dslEquipment
    protected final String tag
    protected String staticId
    protected double startTime

    AbstractEventModelBuilder(Network network, DslEquipment<T> dslEquipment, String tag) {
        super(network)
        this.dslEquipment = dslEquipment
        this.tag = tag
    }

    void staticId(String staticId) {
        dslEquipment.addEquipment(staticId, this::findEquipment)
    }

    void startTime(double startTime) {
        this.startTime = startTime
    }

    @Override
    protected void checkData() {
        isInstantiable &= dslEquipment.checkEquipmentData(LOGGER)
        if (!startTime) {
            LOGGER.warn("'startTime' field is not set")
            isInstantiable = false
        }
    }

    abstract protected Identifiable findEquipment(String staticId)

    @Override
    protected String getLib() {
        tag
    }

    @Override
    abstract EventModel build()
}

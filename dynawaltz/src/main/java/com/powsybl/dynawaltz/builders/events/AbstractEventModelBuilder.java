/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.events;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.dynawaltz.models.events.AbstractEvent;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractEventModelBuilder<T extends Identifiable<?>, R extends AbstractEventModelBuilder<T, R>> extends AbstractDynamicModelBuilder implements ModelBuilder<EventModel> {

    protected final DslEquipment<T> dslEquipment;
    protected final String tag;
    protected String staticId;
    protected Double startTime;

    protected AbstractEventModelBuilder(Network network, DslEquipment<T> dslEquipment, String tag, Reporter reporter) {
        super(network, reporter);
        this.dslEquipment = dslEquipment;
        this.tag = tag;
    }

    public R staticId(String staticId) {
        dslEquipment.addEquipment(staticId, this::findEquipment);
        return self();
    }

    public R startTime(double startTime) {
        this.startTime = startTime;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable &= dslEquipment.checkEquipmentData(reporter);
        if (startTime == null) {
            Reporters.reportFieldNotSet(reporter, "startTime");
            isInstantiable = false;
        }
    }

    protected abstract T findEquipment(String staticId);

    @Override
    public String getModelId() {
        return AbstractEvent.generateEventId(tag + "_", dslEquipment.getStaticId() != null ? dslEquipment.getStaticId() : "unknownStaticId");
    }

    protected abstract R self();
}

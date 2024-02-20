/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;

import java.util.function.Consumer;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEvent extends AbstractPureDynamicBlackBoxModel implements EventModel {

    private final Identifiable<? extends Identifiable<?>> equipment;
    private final double startTime;

    protected AbstractEvent(String eventId, Identifiable<?> equipment, double startTime, String lib) {
        super(eventId, lib);
        this.equipment = equipment;
        this.startTime = startTime;
    }

    public Identifiable<?> getEquipment() {
        return equipment;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }

    @Override
    public void createDynamicModelParameters(DynaWaltzContext context, Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        createEventSpecificParameters(paramSet);
        parametersAdder.accept(paramSet);
    }

    protected abstract void createEventSpecificParameters(ParametersSet paramSet);
}

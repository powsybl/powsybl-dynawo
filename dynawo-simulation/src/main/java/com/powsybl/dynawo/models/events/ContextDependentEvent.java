/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ContextDependentEvent {

    Identifiable<?> getEquipment();

    //TODO delete
    default boolean hasDynamicModel(DynawoSimulationContext context) {
        return true;
    }

    //TODO delete
    void setEquipmentHasDynamicModel(DynawoSimulationContext context);

    default void setEquipmentHasDynamicModel(boolean hasDynamicModel) {
        //TODO implements method
    }
}

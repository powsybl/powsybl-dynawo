/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.info;

import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DynawoEquipmentModelInfoAdder<I extends Identifiable<I>> extends DynawoModelInfoAdder<I, DynawoEquipmentModelInfo<I>, DynawoEquipmentModelInfoAdder<I>> {

    @Override
    default Class<DynawoEquipmentModelInfo> getExtensionClass() {
        return DynawoEquipmentModelInfo.class;
    }

    DynawoEquipmentModelInfoAdder<I> setModelName(String modelName);

    DynawoEquipmentModelInfoAdder<I> setParameterSetId(String parameterSetId);
}

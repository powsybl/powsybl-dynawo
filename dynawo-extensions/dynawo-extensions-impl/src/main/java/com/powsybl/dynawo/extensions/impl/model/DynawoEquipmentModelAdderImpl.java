/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModel;
import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModelAdder;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoEquipmentModelAdderImpl<I extends Identifiable<I>> extends AbstractDynawoModelAdder<I, DynawoEquipmentModel<I>, DynawoEquipmentModelAdderImpl<I>>
        implements DynawoEquipmentModelAdder<I> {

    public DynawoEquipmentModelAdderImpl(I identifiable) {
        super(identifiable);
    }

    @Override
    protected DynawoEquipmentModel<I> createExtension(I extendable) {
        return new DynawoEquipmentModelImpl<>(extendable, modelName, parameterSetId);
    }

    @Override
    protected DynawoEquipmentModelAdderImpl<I> self() {
        return this;
    }
}

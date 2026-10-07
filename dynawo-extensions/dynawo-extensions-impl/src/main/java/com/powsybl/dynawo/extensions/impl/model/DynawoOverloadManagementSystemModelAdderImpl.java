/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoOverloadManagementSystemModelAdderImpl<B extends Branch<B>>
        extends AbstractDynawoAutomationSystemModelAdder<B, DynawoOverloadManagementSystemModel<B>, DynawoOverloadManagementSystemModelAdderImpl<B>>
        implements DynawoOverloadManagementSystemModelAdder<B> {

    private String iMeasurement;
    private TwoSides iMeasurementSide;

    public DynawoOverloadManagementSystemModelAdderImpl(B extendable) {
        super(extendable);
    }

    @Override
    public DynawoOverloadManagementSystemModelAdder<B> withIMeasurement(String iMeasurement) {
        this.iMeasurement = iMeasurement;
        return self();
    }

    @Override
    public DynawoOverloadManagementSystemModelAdder<B> withIMeasurementSide(TwoSides iMeasurementSide) {
        this.iMeasurementSide = iMeasurementSide;
        return self();
    }

    @Override
    protected DynawoOverloadManagementSystemModel<B> createExtension(B extendable) {
        return new DynawoOverloadManagementSystemModelImpl<>(extendable, modelName, parameterSetId, dynamicModelId,
                iMeasurement, iMeasurementSide);
    }

    @Override
    protected DynawoOverloadManagementSystemModelAdderImpl<B> self() {
        return this;
    }
}

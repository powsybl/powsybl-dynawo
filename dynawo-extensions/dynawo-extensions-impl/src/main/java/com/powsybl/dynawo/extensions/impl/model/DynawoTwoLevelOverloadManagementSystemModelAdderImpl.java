/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoTwoLevelOverloadManagementSystemModelAdderImpl<B extends Branch<B>>
        extends AbstractDynawoAutomationSystemModelAdder<B, DynawoTwoLevelOverloadManagementSystemModel<B>, DynawoTwoLevelOverloadManagementSystemModelAdderImpl<B>>
        implements DynawoTwoLevelOverloadManagementSystemModelAdder<B> {

    private String iMeasurement;
    private TwoSides iMeasurementSide;
    private String iMeasurement2;
    private TwoSides iMeasurement2Side;

    public DynawoTwoLevelOverloadManagementSystemModelAdderImpl(B extendable) {
        super(extendable);
    }

    @Override
    public DynawoTwoLevelOverloadManagementSystemModelAdder<B> withIMeasurement1(String iMeasurement) {
        this.iMeasurement = iMeasurement;
        return self();
    }

    @Override
    public DynawoTwoLevelOverloadManagementSystemModelAdder<B> withIMeasurement1Side(TwoSides iMeasurementSide) {
        this.iMeasurementSide = iMeasurementSide;
        return self();
    }

    @Override
    public DynawoTwoLevelOverloadManagementSystemModelAdder<B> withIMeasurement2(String iMeasurement2) {
        this.iMeasurement2 = iMeasurement2;
        return self();
    }

    @Override
    public DynawoTwoLevelOverloadManagementSystemModelAdder<B> withIMeasurement2Side(TwoSides iMeasurement2Side) {
        this.iMeasurement2Side = iMeasurement2Side;
        return self();
    }

    @Override
    protected DynawoTwoLevelOverloadManagementSystemModel<B> createExtension(B extendable) {
        return new DynawoTwoLevelOverloadManagementSystemModelImpl<>(extendable, modelName, parameterSetId, dynamicModelId,
                iMeasurement, iMeasurementSide, iMeasurement2, iMeasurement2Side);
    }

    @Override
    protected DynawoTwoLevelOverloadManagementSystemModelAdderImpl<B> self() {
        return this;
    }
}

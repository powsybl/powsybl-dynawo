/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.overloadmanagments;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.automationsystems.AbstractAutomationSystemModelBuilder;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractOverloadManagementSystemBuilder<T extends AbstractAutomationSystemModelBuilder<T>> extends AbstractAutomationSystemModelBuilder<T> {

    protected static final String BRANCH_TYPE = "BRANCH";

    protected final BuilderEquipment<Branch<?>> iMeasurement;
    protected TwoSides iMeasurementSide;
    protected final BuilderEquipment<Branch<?>> controlledEquipment;

    protected AbstractOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, Reporter reporter, BuilderEquipment<Branch<?>> iMeasurement, BuilderEquipment<Branch<?>> controlledEquipment) {
        super(network, modelConfig, reporter);
        this.iMeasurement = iMeasurement;
        this.controlledEquipment = controlledEquipment;
    }

    public T controlledBranch(String staticId) {
        controlledEquipment.addEquipment(staticId, network::getBranch);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= controlledEquipment.checkEquipmentData(reporter);
        isInstantiable &= iMeasurement.checkEquipmentData(reporter);
        if (iMeasurementSide == null) {
            Reporters.reportFieldNotSet(reporter, "iMeasurementSide");
            isInstantiable = false;
        }
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.overloadmanagments;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.models.automationsystems.AbstractAutomationSystemModelBuilder;
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

    protected AbstractOverloadManagementSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode, BuilderEquipment<Branch<?>> iMeasurement, BuilderEquipment<Branch<?>> controlledEquipment) {
        super(network, modelConfig, reportNode);
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
        isInstantiable &= controlledEquipment.checkEquipmentData(reportNode);
        isInstantiable &= iMeasurement.checkEquipmentData(reportNode);
        if (iMeasurementSide == null) {
            BuilderReports.reportFieldNotSet(reportNode, "iMeasurementSide");
            isInstantiable = false;
        }
    }
}

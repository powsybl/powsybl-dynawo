/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractLoadModelBuilder<R extends AbstractEquipmentModelBuilder<Load, R>> extends AbstractEquipmentModelBuilder<Load, R> {

    private static final BuilderEquipment.EquipmentPredicate<Load> IS_NOT_FICTITIOUS = (eq, f, r) -> {
        if (eq.isFictitious()) {
            BuilderReports.reportFictitiousEquipment(r, f, eq.getId());
            return false;
        }
        return true;
    };

    protected AbstractLoadModelBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.LOAD.toString(), IS_NOT_FICTITIOUS, reportNode);
    }

    @Override
    protected Load findEquipment(String staticId) {
        return network.getLoad(staticId);
    }
}

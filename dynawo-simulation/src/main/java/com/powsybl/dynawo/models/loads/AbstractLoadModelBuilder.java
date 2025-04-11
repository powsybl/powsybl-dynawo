/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractLoadModelBuilder<R extends AbstractEquipmentModelBuilder<Load, R>> extends AbstractEquipmentModelBuilder<Load, R> {

    private static final EquipmentPredicate<Load> IS_NOT_FICTITIOUS = (eq, f, r) -> {
        if (eq.isFictitious()) {
            BuilderReports.reportFictitiousEquipment(r, f, eq.getId());
            return false;
        }
        return true;
    };

    protected AbstractLoadModelBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.LOAD, reportNode);
    }

    @Override
    public R staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment, IS_NOT_FICTITIOUS);
        return self();
    }

    @Override
    public R equipment(Load equipment) {
        builderEquipment.addEquipment(equipment, hasSameNetwork, IS_NOT_FICTITIOUS);
        return self();
    }

    @Override
    protected Load findEquipment(String staticId) {
        return network.getLoad(staticId);
    }
}

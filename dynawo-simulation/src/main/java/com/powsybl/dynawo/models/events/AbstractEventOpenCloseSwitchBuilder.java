/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractEventOpenCloseSwitchBuilder<R extends AbstractEventModelBuilder<Switch, R>> extends AbstractEventModelBuilder<Switch, R> {

    private static final EquipmentChecker<Switch> IS_BREAKER = (eq, f, r) -> switch (eq.getKind()) {
        case BREAKER, LOAD_BREAK_SWITCH -> true;
        case DISCONNECTOR -> {
            BuilderReports.reportWrongSwitchKind(r, f, eq.getId());
            yield false;
        }
    };

    AbstractEventOpenCloseSwitchBuilder(Network network, ReportNode reportNode) {
        super(network, IdentifiableType.SWITCH.toString(), reportNode);
    }

    @Override
    public R staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment, IS_BREAKER);
        eventId = generateEventId(staticId);
        return self();
    }

    @Override
    protected Switch findEquipment(String staticId) {
        return network.getSwitch(staticId);
    }
}

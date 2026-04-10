/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EquipmentChecker;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventOpenSwitchBuilder extends AbstractEventModelBuilder<Switch, EventOpenSwitchBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("EventConnectedStatus", "OpenSwitch", "Open a breaker or a load break switch");

    private static final EquipmentChecker<Switch> IS_BREAKER = (eq, f, r) -> {
        if (SwitchKind.DISCONNECTOR == eq.getKind()) {
            BuilderReports.reportDisconnector(r, f, eq.getId());
            return false;
        }
        return true;
    };

    public static EventOpenSwitchBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventOpenSwitchBuilder of(Network network, ReportNode parentReportNode) {
        return new EventOpenSwitchBuilder(network, parentReportNode);
    }

    public static ModelInfo getModelInfo() {
        return MODEL_INFO;
    }

    /**
     * Returns the model info if usable with the given {@link DynawoVersion}
     */
    public static ModelInfo getModelInfo(DynawoVersion dynawoVersion) {
        return MODEL_INFO.version().includes(dynawoVersion) ? MODEL_INFO : null;
    }

    EventOpenSwitchBuilder(Network network, ReportNode reportNode) {
        super(network, IdentifiableType.SWITCH.toString(), reportNode);
    }

    @Override
    public EventOpenSwitchBuilder staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment, IS_BREAKER);
        eventId = generateEventId(staticId);
        return self();
    }

    @Override
    protected Switch findEquipment(String staticId) {
        return network.getSwitch(staticId);
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    public EventSwitchOpen build() {
        return isInstantiable() ? new EventSwitchOpen(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, true) : null;
    }

    @Override
    protected EventOpenSwitchBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventCloseSwitchBuilder extends AbstractEventOpenCloseSwitchBuilder<EventCloseSwitchBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("EventConnectedStatus", "CloseSwitch", "Close a breaker or a load break switch");

    public static EventCloseSwitchBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventCloseSwitchBuilder of(Network network, ReportNode parentReportNode) {
        return new EventCloseSwitchBuilder(network, parentReportNode);
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

    EventCloseSwitchBuilder(Network network, ReportNode reportNode) {
        super(network, reportNode);
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    public EventSwitchOpen build() {
        return isInstantiable() ? new EventSwitchOpen(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, false) : null;
    }

    @Override
    protected EventCloseSwitchBuilder self() {
        return this;
    }
}

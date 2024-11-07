/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NodeFaultEventBuilder extends AbstractEventModelBuilder<Bus, NodeFaultEventBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("NodeFault", "Node fault with configurable resistance, reactance and duration");

    protected double faultTime;
    protected double rPu;
    protected double xPu;

    public static NodeFaultEventBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static NodeFaultEventBuilder of(Network network, ReportNode reportNode) {
        return new NodeFaultEventBuilder(network, reportNode);
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

    NodeFaultEventBuilder(Network network, ReportNode reportNode) {
        super(network, new BuilderEquipment<>(IdentifiableType.BUS), reportNode);
    }

    public NodeFaultEventBuilder faultTime(double faultTime) {
        this.faultTime = faultTime;
        return self();
    }

    public NodeFaultEventBuilder rPu(double rPu) {
        this.rPu = rPu;
        return self();
    }

    public NodeFaultEventBuilder xPu(double xPu) {
        this.xPu = xPu;
        return self();
    }

    protected Bus findEquipment(String staticId) {
        return network.getBusBreakerView().getBus(staticId);
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (faultTime <= 0) {
            BuilderReports.reportCrossThreshold(reportNode, "faultTime", faultTime, "strictly positive");
            isInstantiable = false;
        }
        if (rPu < 0) {
            BuilderReports.reportCrossThreshold(reportNode, "rPu", rPu, "positive");
            isInstantiable = false;
        }
        if (xPu < 0) {
            BuilderReports.reportCrossThreshold(reportNode, "xPu", xPu, "positive");
            isInstantiable = false;
        }
    }

    @Override
    public NodeFaultEvent build() {
        return isInstantiable() ? new NodeFaultEvent(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, faultTime, rPu, xPu) : null;
    }

    protected NodeFaultEventBuilder self() {
        return this;
    }
}

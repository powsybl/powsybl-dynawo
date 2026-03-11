/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NodeFaultEventBuilder extends AbstractEventModelBuilder<Identifiable<?>, NodeFaultEventBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("NodeFault", "Node fault with configurable resistance, reactance and duration");

    protected double faultTime;
    protected double rPu;
    protected double xPu;
    private IdentifiableType identifiableType;

    public static NodeFaultEventBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static NodeFaultEventBuilder of(Network network, ReportNode parentReportNode) {
        return new NodeFaultEventBuilder(network, parentReportNode);
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
        super(network, "GENERATOR/BUS", reportNode);
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

    @Override
    protected Identifiable<?> findEquipment(String staticId) {
        Identifiable<?> equipment = network.getGenerator(staticId);
        return equipment != null ? equipment : network.getBusBreakerView().getBus(staticId);
    }

    @Override
    public NodeFaultEventBuilder staticId(String staticId) {
        Identifiable<?> eq = findEquipment(staticId);

        if (eq == null) {
            isInstantiable = false;
            return self();
        }

        builderEquipment.addEquipment(staticId, id -> eq);

        this.identifiableType = typeOf(eq);

        eventId = generateEventId(staticId, identifiableType);
        return self();
    }

    private IdentifiableType typeOf(Identifiable<?> eq) {
        if (eq instanceof Generator) {
            return IdentifiableType.GENERATOR;
        }
        if (eq instanceof Bus) {
            return IdentifiableType.BUS;
        }
        throw new PowsyblException("Unsupported equipment type for NodeFaultEvent: " + eq.getClass().getSimpleName());
    }

    public String generateEventId(String staticId, IdentifiableType identifiableType) {
        return getModelName() + "_" + identifiableType.toString() + "_" + staticId;
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (faultTime <= 0) {
            BuilderReports.reportCrossThreshold(reportNode, "faultTime", faultTime, "t > 0");
            isInstantiable = false;
        }
        if (rPu < 0) {
            BuilderReports.reportCrossThreshold(reportNode, "rPu", rPu, "rPu >= 0");
            isInstantiable = false;
        }
        if (xPu < 0) {
            BuilderReports.reportCrossThreshold(reportNode, "xPu", xPu, "xPu >= 0");
            isInstantiable = false;
        }
    }

    @Override
    public NodeFaultEvent build() {
        return isInstantiable() ? new NodeFaultEvent(eventId, builderEquipment.getEquipment(), identifiableType, MODEL_INFO, startTime, faultTime, rPu, xPu) : null;
    }

    protected NodeFaultEventBuilder self() {
        return this;
    }
}

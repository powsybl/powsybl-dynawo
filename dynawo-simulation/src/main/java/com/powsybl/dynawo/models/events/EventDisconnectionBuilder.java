/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.utils.EnergizedUtils;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventDisconnectionBuilder extends AbstractEventConnectionDisconnectionBuilder<EventDisconnectionBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Disconnect", "Disconnects a bus, a branch, an injection or an HVDC line.");

    public static EventDisconnectionBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventDisconnectionBuilder of(Network network, ReportNode parentReportNode) {
        return new EventDisconnectionBuilder(network, parentReportNode);
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

    EventDisconnectionBuilder(Network network, ReportNode reportNode) {
        super(network, "Disconnectable equipment", reportNode);
    }

    public EventDisconnectionBuilder disconnectOnly(TwoSides side) {
        return sideOnly(side);
    }

    @Override
    protected EventModelInfo getEventModelInfo() {
        return MODEL_INFO;
    }

    @Override
    protected String getSideFieldName() {
        return "disconnectOnly";
    }

    @Override
    protected boolean isDisconnect() {
        return true;
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (builderEquipment.hasEquipment()) {
            setType(builderEquipment.getEquipment().getType());
            switch (type) {
                case BUS -> {
                    Bus bus = (Bus) builderEquipment.getEquipment();
                    if (!EnergizedUtils.isEnergizedAndInMainConnectedComponent(bus)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                    if (side != null) {
                        BuilderReports.reportFieldSetWithWrongEquipment(reportNode, "disconnectOnly", bus.getType(), bus.getId());
                        isInstantiable = false;
                    }
                }
                case INJECTION -> {
                    Injection<?> injection = (Injection<?>) builderEquipment.getEquipment();
                    if (!EnergizedUtils.isEnergizedAndInMainConnectedComponent(injection)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                    if (side != null) {
                        BuilderReports.reportFieldSetWithWrongEquipment(reportNode, "disconnectOnly", injection.getType(), injection.getId());
                        isInstantiable = false;
                    }
                }
                case BRANCH -> {
                    Branch<?> branch = (Branch<?>) builderEquipment.getEquipment();
                    if (side != null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(branch, side)
                            || side == null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(branch)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                }
                case HVDC -> {
                    HvdcLine hvdcLine = (HvdcLine) builderEquipment.getEquipment();
                    if (side != null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(hvdcLine, side)
                            || side == null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(hvdcLine)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                }
                case NONE -> {
                    BuilderReports.reportStaticIdUnknown(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId(), "Disconnectable equipment");
                    isInstantiable = false;
                }
            }
        }
    }

    @Override
    protected EventDisconnectionBuilder self() {
        return this;
    }
}

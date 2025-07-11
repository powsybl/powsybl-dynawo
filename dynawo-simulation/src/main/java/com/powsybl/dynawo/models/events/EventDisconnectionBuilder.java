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
public class EventDisconnectionBuilder extends AbstractEventModelBuilder<Identifiable<?>, EventDisconnectionBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Disconnect", "Disconnects a bus, a branch, an injection or an HVDC line");
    private static final String STATIC_ID_FIELD_NAME = "staticId";

    private enum DisconnectionType {
        BUS,
        INJECTION,
        BRANCH,
        HVDC,
        NONE
    }

    private DisconnectionType disconnectionType = DisconnectionType.NONE;
    protected TwoSides disconnectSide = null;

    public static EventDisconnectionBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventDisconnectionBuilder of(Network network, ReportNode reportNode) {
        return new EventDisconnectionBuilder(network, reportNode);
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
        super(network, new BuilderEquipment<>("Disconnectable equipment"), reportNode);
    }

    public EventDisconnectionBuilder disconnectOnly(TwoSides side) {
        this.disconnectSide = side;
        return self();
    }

    private void setDisconnectionType(IdentifiableType type) {
        disconnectionType = switch (type) {
            case BUS -> DisconnectionType.BUS;
            case HVDC_LINE -> DisconnectionType.HVDC;
            case GENERATOR, LOAD, STATIC_VAR_COMPENSATOR, SHUNT_COMPENSATOR -> DisconnectionType.INJECTION;
            case LINE, TWO_WINDINGS_TRANSFORMER -> DisconnectionType.BRANCH;
            default -> DisconnectionType.NONE;
        };
    }

    @Override
    protected Identifiable<?> findEquipment(String staticId) {
        return network.getIdentifiable(staticId);
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (builderEquipment.hasEquipment()) {
            setDisconnectionType(builderEquipment.getEquipment().getType());
            switch (disconnectionType) {
                case BUS -> {
                    Bus bus = (Bus) builderEquipment.getEquipment();
                    if (!EnergizedUtils.isEnergizedAndInMainConnectedComponent(bus)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                    if (disconnectSide != null) {
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
                    if (disconnectSide != null) {
                        BuilderReports.reportFieldSetWithWrongEquipment(reportNode, "disconnectOnly", injection.getType(), injection.getId());
                        isInstantiable = false;
                    }
                }
                case BRANCH -> {
                    Branch<?> branch = (Branch<?>) builderEquipment.getEquipment();
                    if (disconnectSide != null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(branch, disconnectSide)
                            || disconnectSide == null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(branch)) {
                        BuilderReports.reportNotEnergized(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId());
                        isInstantiable = false;
                    }
                }
                case HVDC -> {
                    HvdcLine hvdcLine = (HvdcLine) builderEquipment.getEquipment();
                    if (disconnectSide != null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(hvdcLine, disconnectSide)
                            || disconnectSide == null && !EnergizedUtils.isEnergizedAndInMainConnectedComponent(hvdcLine)) {
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
    public AbstractEvent build() {
        if (isInstantiable()) {
            return switch (disconnectionType) {
                case INJECTION -> new EventInjectionDisconnection(eventId, (Injection<?>) builderEquipment.getEquipment(), MODEL_INFO, startTime, true);
                case BRANCH ->
                        new EventBranchDisconnection(eventId, (Branch<?>) builderEquipment.getEquipment(), MODEL_INFO, startTime, disconnectSide);
                case HVDC ->
                        new EventHvdcDisconnection(eventId, (HvdcLine) builderEquipment.getEquipment(), MODEL_INFO, startTime, disconnectSide);
                case BUS ->
                        new EventBusDisconnection(eventId, (Bus) builderEquipment.getEquipment(), MODEL_INFO, startTime, true);
                default -> null;
            };
        }
        return null;
    }

    @Override
    protected EventDisconnectionBuilder self() {
        return this;
    }
}

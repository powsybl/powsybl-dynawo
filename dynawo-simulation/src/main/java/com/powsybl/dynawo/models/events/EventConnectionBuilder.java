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
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventConnectionBuilder extends AbstractEventConnectionDisconnectionBuilder<EventConnectionBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Connect", "Connects a bus, a branch, an injection or an HVDC line.");

    public static EventConnectionBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventConnectionBuilder of(Network network, ReportNode parentReportNode) {
        return new EventConnectionBuilder(network, parentReportNode);
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

    EventConnectionBuilder(Network network, ReportNode reportNode) {
        super(network, "Connectable equipment", reportNode);
    }

    public EventConnectionBuilder connectOnly(TwoSides side) {
        this.side = side;
        return self();
    }

    @Override
    protected EventModelInfo getEventModelInfo() {
        return MODEL_INFO;
    }

    @Override
    protected String getSideFieldName() {
        return "connectOnly";
    }

    @Override
    protected boolean isDisconnect() {
        return false;
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (builderEquipment.hasEquipment()) {
            setType(builderEquipment.getEquipment().getType());
            switch (type) {
                case BUS -> {
                    Bus bus = (Bus) builderEquipment.getEquipment();
                    if (side != null) {
                        BuilderReports.reportFieldSetWithWrongEquipment(reportNode, getSideFieldName(), bus.getType(), bus.getId());
                        isInstantiable = false;
                    }
                }
                case INJECTION -> {
                    Injection<?> injection = (Injection<?>) builderEquipment.getEquipment();
                    if (side != null) {
                        BuilderReports.reportFieldSetWithWrongEquipment(reportNode, getSideFieldName(), injection.getType(), injection.getId());
                        isInstantiable = false;
                    }
                }
                case NONE -> {
                    BuilderReports.reportStaticIdUnknown(reportNode, STATIC_ID_FIELD_NAME, builderEquipment.getStaticId(), getModelName());
                    isInstantiable = false;
                }
            }
        }
    }

    @Override
    protected EventConnectionBuilder self() {
        return this;
    }
}

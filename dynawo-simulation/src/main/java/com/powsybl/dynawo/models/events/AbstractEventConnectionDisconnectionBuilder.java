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
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEventConnectionDisconnectionBuilder<S extends AbstractEventConnectionDisconnectionBuilder<S>> extends AbstractEventModelBuilder<Identifiable<?>, S> {

    protected static final String STATIC_ID_FIELD_NAME = "staticId";

    protected enum ConnectionType {
        BUS,
        INJECTION,
        BRANCH,
        HVDC,
        NONE
    }
    protected ConnectionType type = ConnectionType.NONE;
    protected TwoSides side = null;

    protected AbstractEventConnectionDisconnectionBuilder(Network network, String equipmentDescription, ReportNode reportNode) {
        super(network, equipmentDescription, reportNode);
    }

    protected abstract EventModelInfo getEventModelInfo();

    protected abstract String getSideFieldName();

    protected S sideOnly(TwoSides side) {
        this.side = side;
        return self();
    }

    protected void setType(IdentifiableType type) {
        this.type = switch (type) {
            case BUS -> ConnectionType.BUS;
            case HVDC_LINE -> ConnectionType.HVDC;
            case GENERATOR, LOAD, STATIC_VAR_COMPENSATOR, SHUNT_COMPENSATOR -> ConnectionType.INJECTION;
            case LINE, TWO_WINDINGS_TRANSFORMER -> ConnectionType.BRANCH;
            default -> ConnectionType.NONE;
        };
    }

    @Override
    protected Identifiable<?> findEquipment(String staticId) {
        return network.getIdentifiable(staticId);
    }

    @Override
    protected String getModelName() {
        return getEventModelInfo().name();
    }

    abstract protected boolean isDisconnect();

    @Override
    public AbstractEvent build() {
        if (isInstantiable()) {
            return switch (type) {
                case INJECTION -> new EventInjectionDisconnection(eventId, (Injection<?>) builderEquipment.getEquipment(), getEventModelInfo(), startTime, isDisconnect());
                case BRANCH ->
                        new EventBranchDisconnection(eventId, (Branch<?>) builderEquipment.getEquipment(), getEventModelInfo(), startTime, new DisconnectionSide(side, isDisconnect()));
                case HVDC ->
                        new EventHvdcDisconnection(eventId, (HvdcLine) builderEquipment.getEquipment(), getEventModelInfo(), startTime, new DisconnectionSide(side, isDisconnect()));
                case BUS ->
                        new EventBusDisconnection(eventId, (Bus) builderEquipment.getEquipment(), getEventModelInfo(), startTime, isDisconnect());
                default -> null;
            };
        }
        return null;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.*;

import java.util.EnumSet;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventDisconnectionBuilder extends AbstractEventModelBuilder<Identifiable<?>, EventDisconnectionBuilder> {

    public static final String TAG = "Disconnect";
    private static final EnumSet<IdentifiableType> CONNECTABLE_INJECTIONS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD, IdentifiableType.STATIC_VAR_COMPENSATOR, IdentifiableType.SHUNT_COMPENSATOR);
    private static final EnumSet<IdentifiableType> CONNECTABLE_QUADRIPOLES = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER);

    private enum DisconnectionType {
        INJECTION,
        QUADRIPOLE,
        HVDC,
        NONE
    }

    private boolean disconnectSide = false;
    private DisconnectionType disconnectionType = DisconnectionType.NONE;
    protected boolean disconnectOrigin = true;
    protected boolean disconnectExtremity = true;

    public EventDisconnectionBuilder(Network network, Reporter reporter) {
        super(network, new DslEquipment<>("Disconnectable equipment"), TAG, reporter);
    }

    public EventDisconnectionBuilder disconnectOnly(TwoSides side) {
        disconnectSide = true;
        switch (side) {
            case ONE -> {
                disconnectOrigin = true;
                disconnectExtremity = false;
            }
            case TWO -> {
                disconnectOrigin = false;
                disconnectExtremity = true;
            }
        }
        return self();
    }

    private void setDisconnectionType(IdentifiableType type) {
        if (CONNECTABLE_INJECTIONS.contains(type)) {
            disconnectionType = DisconnectionType.INJECTION;
        } else if (CONNECTABLE_QUADRIPOLES.contains(type)) {
            disconnectionType = DisconnectionType.QUADRIPOLE;
        } else if (IdentifiableType.HVDC_LINE == type) {
            disconnectionType = DisconnectionType.HVDC;
        }
    }

    @Override
    protected Identifiable<?> findEquipment(String staticId) {
        return network.getIdentifiable(staticId);
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (dslEquipment.hasEquipment()) {
            setDisconnectionType(dslEquipment.getEquipment().getType());
            if (disconnectionType == DisconnectionType.NONE) {
                Reporters.reportStaticIdUnknown(reporter, "staticId", dslEquipment.getStaticId(), "Disconnectable equipment");
                isInstantiable = false;
            }
            if (DisconnectionType.INJECTION == disconnectionType && disconnectSide) {
                Reporters.reportFieldSetWithWrongEquipment(reporter, "disconnectSide", dslEquipment.getEquipment().getType(), dslEquipment.getStaticId());
                isInstantiable = false;
            }
        }
    }

    @Override
    public AbstractEvent build() {
        if (isInstantiable()) {
            return switch (disconnectionType) {
                case INJECTION -> new EventInjectionDisconnection((Injection<?>) dslEquipment.getEquipment(), startTime, true);
                case QUADRIPOLE ->
                        new EventQuadripoleDisconnection((Branch<?>) dslEquipment.getEquipment(), startTime, disconnectOrigin, disconnectExtremity);
                case HVDC ->
                        new EventHvdcDisconnection((HvdcLine) dslEquipment.getEquipment(), startTime, disconnectOrigin, disconnectExtremity);
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
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
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariationBuilder extends AbstractEventModelBuilder<Injection<?>, EventActivePowerVariationBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Step", "Power variation on generator or load");

    protected Double deltaP;

    public static EventActivePowerVariationBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventActivePowerVariationBuilder of(Network network, ReportNode reportNode) {
        return new EventActivePowerVariationBuilder(network, reportNode);
    }

    public static EventModelInfo getEventModelInfo() {
        return MODEL_INFO;
    }

    EventActivePowerVariationBuilder(Network network, ReportNode reportNode) {
        super(network, new BuilderEquipment<>("GENERATOR/LOAD"), reportNode);
    }

    public EventActivePowerVariationBuilder deltaP(double deltaP) {
        this.deltaP = deltaP;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaP == null) {
            BuilderReports.reportFieldNotSet(reportNode, "deltaP");
            isInstantiable = false;
        }
    }

    @Override
    protected Injection<?> findEquipment(String staticId) {
        Injection<?> equipment = network.getLoad(staticId);
        return equipment != null ? equipment : network.getGenerator(staticId);
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    public EventActivePowerVariation build() {
        return isInstantiable() ? new EventActivePowerVariation(eventId, builderEquipment.getEquipment(), startTime, deltaP) : null;
    }

    @Override
    protected EventActivePowerVariationBuilder self() {
        return this;
    }
}

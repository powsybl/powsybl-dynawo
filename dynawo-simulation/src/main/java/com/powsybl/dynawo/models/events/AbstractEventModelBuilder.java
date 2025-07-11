/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawo.builders.AbstractDynamicModelBuilder;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.ModelBuilder;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractEventModelBuilder<T extends Identifiable<?>, R extends AbstractEventModelBuilder<T, R>> extends AbstractDynamicModelBuilder implements ModelBuilder<EventModel> {

    protected final BuilderEquipment<T> builderEquipment;
    protected String eventId;
    protected String staticId;
    protected Double startTime;

    protected AbstractEventModelBuilder(Network network, String fieldName, ReportNode parentReportNode) {
        super(network, parentReportNode);
        this.builderEquipment = new BuilderEquipment<>(fieldName, reportNode);
        this.eventId = generateDefaultEventId();
    }

    public R staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment);
        eventId = generateEventId(staticId);
        return self();
    }

    public R startTime(double startTime) {
        this.startTime = startTime;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable &= builderEquipment.checkEquipmentData();
        if (startTime == null) {
            BuilderReports.reportFieldNotSet(reportNode, "startTime");
            isInstantiable = false;
        }
    }

    protected abstract T findEquipment(String staticId);

    private String generateEventId(String staticId) {
        return getModelName() + "_" + staticId;
    }

    protected String generateDefaultEventId() {
        return generateEventId("unknownId");
    }

    @Override
    public String getModelId() {
        return eventId;
    }

    protected abstract R self();
}

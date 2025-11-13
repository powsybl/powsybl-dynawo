/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
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
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EventReactivePowerVariationBuilder extends AbstractEventPowerVariationBuilder<EventReactivePowerVariationBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Step", "ReactivePowerVariation", "Reactive power variation on generator or load");

    public static EventReactivePowerVariationBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventReactivePowerVariationBuilder of(Network network, ReportNode reportNode) {
        return new EventReactivePowerVariationBuilder(network, reportNode);
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

    EventReactivePowerVariationBuilder(Network network, ReportNode parentReportNode) {
        super(network, parentReportNode, "deltaQ");
    }

    public EventReactivePowerVariationBuilder deltaQ(double deltaQ) {
        this.deltaValue = deltaQ;
        return self();
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    public EventReactivePowerVariation build() {
        return isInstantiable() ? new EventReactivePowerVariation(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, deltaValue) : null;
    }

    @Override
    protected EventReactivePowerVariationBuilder self() {
        return this;
    }
}

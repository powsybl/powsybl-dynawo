/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
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
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EventReferenceVoltageVariationBuilder extends AbstractEventPowerBuilder<EventReferenceVoltageVariationBuilder, EventReferenceVoltageVariation> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Step", "ReferenceVoltageVariation", "Reference voltage variation on synchronous/synchronized generator");

    public static EventReferenceVoltageVariationBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static EventReferenceVoltageVariationBuilder of(Network network, ReportNode reportNode) {
        return new EventReferenceVoltageVariationBuilder(network, reportNode);
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

    EventReferenceVoltageVariationBuilder(Network network, ReportNode parentReportNode) {
        super(network, parentReportNode, MODEL_INFO, "deltaU");
    }

    public EventReferenceVoltageVariationBuilder deltaU(double deltaU) {
        this.deltaValue = deltaU;
        return self();
    }

    @Override
    protected String getModelName() {
        return MODEL_INFO.name();
    }

    @Override
    public EventReferenceVoltageVariation build() {
        return isInstantiable() ? new EventReferenceVoltageVariation(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, deltaValue) : null;
    }

    @Override
    protected EventReferenceVoltageVariationBuilder self() {
        return this;
    }
}

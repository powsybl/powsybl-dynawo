/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Riad Benradi {@benradiria <riad.benradi at rte-france.com>}
 */
public class EventReactivePowerVariationBuilder extends AbstractEventModelBuilder<Injection<?>, EventReactivePowerVariationBuilder> {

    private static final EventModelInfo MODEL_INFO = new EventModelInfo("Step", "ReactivePowerVariation", "Reactive power variation on generator or load");

    protected Double deltaQ;

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
        super(network, "GENERATOR/LOAD", parentReportNode);
    }

    public EventReactivePowerVariationBuilder deltaQ(double deltaQ) {
        this.deltaQ = deltaQ;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaQ == null) {
            BuilderReports.reportFieldNotSet(reportNode, "deltaQ");
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
    public EventReactivePowerVariation build() {
        return isInstantiable() ? new EventReactivePowerVariation(eventId, builderEquipment.getEquipment(), MODEL_INFO, startTime, deltaQ) : null;
    }

    @Override
    protected EventReactivePowerVariationBuilder self() {
        return this;
    }
}

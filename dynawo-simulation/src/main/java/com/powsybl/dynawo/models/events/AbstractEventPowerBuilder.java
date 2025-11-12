/**
 *
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public abstract class AbstractEventPowerBuilder<B extends AbstractEventPowerBuilder<B, E>, E extends AbstractEvent>
        extends AbstractEventModelBuilder<Injection<?>, B> {

    protected final EventModelInfo modelInfo;
    protected Double deltaValue;
    protected String deltaFieldName;

    protected AbstractEventPowerBuilder(Network network, ReportNode parentReportNode, EventModelInfo modelInfo, String deltaFieldName) {
        super(network, "GENERATOR/LOAD", parentReportNode);
        this.modelInfo = modelInfo;
        this.deltaFieldName = deltaFieldName;
    }

    @SuppressWarnings("unchecked")
    public B deltaValue(double deltaValue) {
        this.deltaValue = deltaValue;
        return (B) this;
    }

    @Override
    protected Injection<?> findEquipment(String staticId) {
        Injection<?> equipment = network.getLoad(staticId);
        return equipment != null ? equipment : network.getGenerator(staticId);
    }

    @Override
    protected String getModelName() {
        return modelInfo.name();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaValue == null) {
            BuilderReports.reportFieldNotSet(reportNode, deltaFieldName);
            isInstantiable = false;
        }
    }

    @Override
    public abstract E build();
}


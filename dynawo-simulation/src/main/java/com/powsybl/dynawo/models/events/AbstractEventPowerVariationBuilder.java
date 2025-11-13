/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public abstract class AbstractEventPowerVariationBuilder<B extends AbstractEventPowerVariationBuilder<B>>
        extends AbstractEventModelBuilder<Injection<?>, B> {

    protected final String deltaFieldName;
    protected Double deltaValue;

    protected AbstractEventPowerVariationBuilder(Network network, ReportNode parentReportNode, String deltaFieldName) {
        super(network, "GENERATOR/LOAD", parentReportNode);
        this.deltaFieldName = deltaFieldName;
    }

    @Override
    protected Injection<?> findEquipment(String staticId) {
        Injection<?> equipment = network.getLoad(staticId);
        return equipment != null ? equipment : network.getGenerator(staticId);
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaValue == null) {
            BuilderReports.reportFieldNotSet(reportNode, deltaFieldName);
            isInstantiable = false;
        }
    }
}


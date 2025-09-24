/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.iidm.network.Network;

import java.util.Objects;

import static com.powsybl.dynawo.builders.BuilderReports.createModelInstantiationReportNode;
import static com.powsybl.dynawo.builders.BuilderReports.setupModelInstantiation;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicModelBuilder {

    protected final Network network;
    protected final ReportNode reportNode;
    protected boolean isInstantiable = true;

    protected AbstractDynamicModelBuilder(Network network, ReportNode parentReportNode) {
        this.network = Objects.requireNonNull(network);
        this.reportNode = createModelInstantiationReportNode(Objects.requireNonNull(parentReportNode));
    }

    protected abstract void checkData();

    protected final boolean isInstantiable() {
        checkData();
        setupModelInstantiation(reportNode, getModelName(), getModelId(), isInstantiable);
        return isInstantiable;
    }

    protected abstract String getModelName();

    protected abstract String getModelId();
}

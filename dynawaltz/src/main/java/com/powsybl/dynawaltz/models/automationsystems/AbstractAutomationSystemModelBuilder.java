/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.AbstractDynamicModelBuilder;
import com.powsybl.dynawaltz.builders.ModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.Network;

import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractAutomationSystemModelBuilder<T extends AbstractAutomationSystemModelBuilder<T>> extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final ModelConfig modelConfig;

    protected AbstractAutomationSystemModelBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, reportNode);
        this.modelConfig = Objects.requireNonNull(modelConfig);
    }

    public T dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return self();
    }

    public T parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        if (dynamicModelId == null) {
            BuilderReports.reportFieldNotSet(reportNode, "dynamicModelId");
            isInstantiable = false;
        }
        if (parameterSetId == null) {
            BuilderReports.reportFieldNotSet(reportNode, "parameterSetId");
            isInstantiable = false;
        }
    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    protected String getLib() {
        return modelConfig.lib();
    }

    protected abstract T self();
}

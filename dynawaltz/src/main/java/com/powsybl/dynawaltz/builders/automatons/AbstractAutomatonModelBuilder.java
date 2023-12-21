/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.AbstractDynamicModelBuilder;
import com.powsybl.dynawaltz.builders.ModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractAutomatonModelBuilder<T extends AbstractAutomatonModelBuilder<T>> extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final ModelConfig modelConfig;

    public AbstractAutomatonModelBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, reporter);
        this.modelConfig = modelConfig;
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
            Reporters.reportFieldNotSet(reporter, "dynamicModelId");
            isInstantiable = false;
        }
        if (parameterSetId == null) {
            Reporters.reportFieldNotSet(reporter, "dynamicModelId");
            isInstantiable = false;
        }
    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    protected String getLib() {
        return modelConfig.getLib();
    }

    @Override
    public abstract DynamicModel build();

    protected abstract T self();
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class InfiniteBusBuilder extends AbstractBusBuilder<InfiniteBusBuilder> {

    private static final String CATEGORY = "infiniteBuses";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static InfiniteBusBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static InfiniteBusBuilder of(Network network, ReportNode reportNode) {
        return new InfiniteBusBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static InfiniteBusBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static InfiniteBusBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, InfiniteBusBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new InfiniteBusBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected InfiniteBusBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public InfiniteBus build() {
        return isInstantiable() ? new InfiniteBus(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected InfiniteBusBuilder self() {
        return this;
    }
}

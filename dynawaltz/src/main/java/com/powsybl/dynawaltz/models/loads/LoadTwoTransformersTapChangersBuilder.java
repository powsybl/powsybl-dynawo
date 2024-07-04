/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersTapChangersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersTapChangersBuilder> {

    public static final String CATEGORY = "LOAD_TWO_TRANSFORMERS_TAP_CHANGER";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static LoadTwoTransformersTapChangersBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static LoadTwoTransformersTapChangersBuilder of(Network network, ReportNode reportNode) {
        return new LoadTwoTransformersTapChangersBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static LoadTwoTransformersTapChangersBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static LoadTwoTransformersTapChangersBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, LoadTwoTransformersTapChangersBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new LoadTwoTransformersTapChangersBuilder(network, modelConfig, reportNode);
    }

    public static Set<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    protected LoadTwoTransformersTapChangersBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public LoadTwoTransformersTapChangers build() {
        return isInstantiable() ? new LoadTwoTransformersTapChangers(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected LoadTwoTransformersTapChangersBuilder self() {
        return this;
    }
}

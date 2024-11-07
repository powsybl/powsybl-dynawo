/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.transformers;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatioBuilder extends AbstractEquipmentModelBuilder<TwoWindingsTransformer, TransformerFixedRatioBuilder> {

    public static final String CATEGORY = "TRANSFORMER";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static TransformerFixedRatioBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, ReportNode reportNode) {
        return new TransformerFixedRatioBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static TransformerFixedRatioBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, TransformerFixedRatioBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new TransformerFixedRatioBuilder(network, modelConfig, reportNode);
    }

    public static ModelInfo getDefaultModelInfo() {
        return MODEL_CONFIGS.getDefaultModelConfig();
    }

    public static Collection<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    /**
     * Returns models usable with the given {@link DynawoVersion}
     */
    public static Collection<ModelInfo> getSupportedModelInfos(DynawoVersion dynawoVersion) {
        return MODEL_CONFIGS.getModelInfos(dynawoVersion);
    }

    protected TransformerFixedRatioBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.TWO_WINDINGS_TRANSFORMER, reportNode);
    }

    @Override
    protected TwoWindingsTransformer findEquipment(String staticId) {
        return network.getTwoWindingsTransformer(staticId);
    }

    @Override
    public TransformerFixedRatio build() {
        return isInstantiable() ? new TransformerFixedRatio(dynamicModelId, getEquipment(), parameterSetId, modelConfig) : null;
    }

    @Override
    protected TransformerFixedRatioBuilder self() {
        return this;
    }
}

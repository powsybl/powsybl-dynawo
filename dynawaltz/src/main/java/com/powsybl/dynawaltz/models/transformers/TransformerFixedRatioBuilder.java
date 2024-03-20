/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatioBuilder extends AbstractEquipmentModelBuilder<TwoWindingsTransformer, TransformerFixedRatioBuilder> {

    private static final String CATEGORY = "transformers";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static TransformerFixedRatioBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, ReportNode reportNode) {
        return new TransformerFixedRatioBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static TransformerFixedRatioBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, TransformerFixedRatioBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new TransformerFixedRatioBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
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
        return isInstantiable() ? new TransformerFixedRatio(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected TransformerFixedRatioBuilder self() {
        return this;
    }
}

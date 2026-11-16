/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators.iec;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.builders.ModelConfigs;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.generators.AbstractGeneratorBuilder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class IecBuilder extends AbstractGeneratorBuilder<IecBuilder> {

    public static final String CATEGORY = "IEC";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static IecBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static IecBuilder of(Network network, ReportNode reportNode) {
        return new IecBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static IecBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static IecBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName, reportNode);
        return modelConfig != null ? new IecBuilder(network, modelConfig, reportNode) : null;
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

    protected IecBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode);
    }

    @Override
    public AbstractEquipmentBlackBoxModel<Generator> build() {
        if (isInstantiable()) {
            if (modelConfig.hasProperty("WT")) {
                return new WtIecGen(getEquipment(), parameterSetId, modelConfig);
            } else if (modelConfig.hasProperty("WPP")) {
                return new WppIecGen(getEquipment(), parameterSetId, modelConfig);
            }
        }
        return null;
    }

    @Override
    protected IecBuilder self() {
        return this;
    }
}

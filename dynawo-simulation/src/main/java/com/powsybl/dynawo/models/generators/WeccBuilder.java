/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WeccBuilder extends AbstractGeneratorBuilder<WeccBuilder> {

    public static final String CATEGORY = "WECC";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static WeccBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static WeccBuilder of(Network network, ReportNode reportNode) {
        return new WeccBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static WeccBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static WeccBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, WeccBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new WeccBuilder(network, modelConfig, reportNode);
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

    protected WeccBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public WeccGen build() {
        if (isInstantiable()) {
            if (modelConfig.isSynchronized()) {
                return new SynchronizedWeccGen(getEquipment(), parameterSetId, modelConfig, modelConfig.internalModelPrefix());
            } else {
                return new WeccGen(getEquipment(), parameterSetId, modelConfig, modelConfig.internalModelPrefix());
            }
        }
        return null;
    }

    @Override
    protected WeccBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder<SynchronousGeneratorBuilder> {

    public static final String CATEGORY = "SYNCHRONOUS_GENERATOR";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static SynchronousGeneratorBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, ReportNode reportNode) {
        return new SynchronousGeneratorBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static SynchronousGeneratorBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, SynchronousGeneratorBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new SynchronousGeneratorBuilder(network, modelConfig, reportNode);
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

    protected SynchronousGeneratorBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    protected EnumGeneratorComponent getGeneratorComponent() {
        boolean aux = modelConfig.hasAuxiliary();
        boolean transformer = modelConfig.hasTransformer();
        if (aux && transformer) {
            return EnumGeneratorComponent.AUXILIARY_TRANSFORMER;
        } else if (transformer) {
            return EnumGeneratorComponent.TRANSFORMER;
        } else if (aux) {
            throw new PowsyblException("Generator component auxiliary without transformer is not supported");
        }
        return EnumGeneratorComponent.NONE;
    }

    @Override
    public SynchronousGenerator build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new SynchronousGeneratorControllable(getEquipment(), parameterSetId, modelConfig, getGeneratorComponent());
            } else {
                return new SynchronousGenerator(getEquipment(), parameterSetId, modelConfig, getGeneratorComponent());
            }
        }
        return null;
    }

    @Override
    protected SynchronousGeneratorBuilder self() {
        return this;
    }
}

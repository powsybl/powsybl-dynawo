/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.commons.PowsyblException;
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
public class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder<SynchronousGeneratorBuilder> {

    private static final String CATEGORY = "synchronousGenerators";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static SynchronousGeneratorBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, ReportNode reportNode) {
        return new SynchronousGeneratorBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static SynchronousGeneratorBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, SynchronousGeneratorBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new SynchronousGeneratorBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
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
                return new SynchronousGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), getGeneratorComponent());
            } else {
                return new SynchronousGenerator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), getGeneratorComponent());
            }
        }
        return null;
    }

    @Override
    protected SynchronousGeneratorBuilder self() {
        return this;
    }
}

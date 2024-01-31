/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder<SynchronousGeneratorBuilder> {

    private static final String CATEGORY = "synchronousGenerators";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigsNew(CATEGORY);

    public static SynchronousGeneratorBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getDefaultModelConfig();
        if (modelConfig == null) {
            Reporters.reportDefaultLibNotFound(reporter, SynchronousGeneratorBuilder.class.getSimpleName());
            return null;
        }
        return new SynchronousGeneratorBuilder(network, modelConfig, reporter);
    }

    public static SynchronousGeneratorBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static SynchronousGeneratorBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, SynchronousGeneratorBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new SynchronousGeneratorBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected SynchronousGeneratorBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
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

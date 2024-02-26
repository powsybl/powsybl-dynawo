/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

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
public class WeccBuilder extends AbstractGeneratorBuilder<WeccBuilder> {

    private static final String CATEGORY = "wecc";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static WeccBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static WeccBuilder of(Network network, Reporter reporter) {
        return new WeccBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reporter);
    }

    public static WeccBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static WeccBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, WeccBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new WeccBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected WeccBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public WeccGen build() {
        if (isInstantiable()) {
            if (modelConfig.isSynchronized()) {
                return new SynchronizedWeccGen(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), modelConfig.internalModelPrefix());
            } else {
                return new WeccGen(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), modelConfig.internalModelPrefix());
            }
        }
        return null;
    }

    @Override
    protected WeccBuilder self() {
        return this;
    }
}

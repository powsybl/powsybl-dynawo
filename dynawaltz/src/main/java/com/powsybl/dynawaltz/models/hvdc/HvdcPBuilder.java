/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

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
public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    private static final String CATEGORY = "hvdcP";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigsNew(CATEGORY);

    public static HvdcPBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static HvdcPBuilder of(Network network, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getDefaultModelConfig();
        if (modelConfig == null) {
            Reporters.reportDefaultLibNotFound(reporter, HvdcPBuilder.class.getSimpleName());
            return null;
        }
        return new HvdcPBuilder(network, modelConfig, reporter);
    }

    public static HvdcPBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static HvdcPBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, HvdcPBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new HvdcPBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected HvdcPBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public HvdcP build() {
        if (isInstantiable()) {
            if (modelConfig.isDangling()) {
                return new HvdcPDangling(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), danglingSide);
            } else {
                return new HvdcP(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            }
        }
        return null;
    }

    @Override
    protected HvdcPBuilder self() {
        return this;
    }
}

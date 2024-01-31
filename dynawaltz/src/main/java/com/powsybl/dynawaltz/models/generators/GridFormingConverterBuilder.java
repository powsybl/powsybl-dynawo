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
public class GridFormingConverterBuilder extends AbstractGeneratorBuilder<GridFormingConverterBuilder> {

    private static final String CATEGORY = "gridFormingConverters";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigsNew(CATEGORY);

    public static GridFormingConverterBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static GridFormingConverterBuilder of(Network network, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getDefaultModelConfig();
        if (modelConfig == null) {
            Reporters.reportDefaultLibNotFound(reporter, GridFormingConverterBuilder.class.getSimpleName());
            return null;
        }
        return new GridFormingConverterBuilder(network, modelConfig, reporter);
    }

    public static GridFormingConverterBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static GridFormingConverterBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, GridFormingConverterBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new GridFormingConverterBuilder(network, modelConfig, reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected GridFormingConverterBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public GridFormingConverter build() {
        return isInstantiable() ? new GridFormingConverter(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected GridFormingConverterBuilder self() {
        return this;
    }
}

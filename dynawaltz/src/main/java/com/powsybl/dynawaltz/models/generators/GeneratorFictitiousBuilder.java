/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

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
public class GeneratorFictitiousBuilder extends AbstractGeneratorBuilder<GeneratorFictitiousBuilder> {

    private static final String CATEGORY = "baseGenerators";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static GeneratorFictitiousBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static GeneratorFictitiousBuilder of(Network network, ReportNode reportNode) {
        return new GeneratorFictitiousBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static GeneratorFictitiousBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static GeneratorFictitiousBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, GeneratorFictitiousBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new GeneratorFictitiousBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected GeneratorFictitiousBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public GeneratorFictitious build() {
        return isInstantiable() ? new GeneratorFictitious(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected GeneratorFictitiousBuilder self() {
        return this;
    }
}

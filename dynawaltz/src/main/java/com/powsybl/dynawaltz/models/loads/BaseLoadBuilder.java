/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

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
public class BaseLoadBuilder extends AbstractLoadModelBuilder<BaseLoadBuilder> {

    private static final String CATEGORY = "baseLoads";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static BaseLoadBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static BaseLoadBuilder of(Network network, ReportNode reportNode) {
        return new BaseLoadBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static BaseLoadBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static BaseLoadBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, BaseLoadBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new BaseLoadBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected BaseLoadBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public BaseLoad build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new BaseLoadControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            } else {
                return new BaseLoad(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            }
        } else {
            return null;
        }
    }

    @Override
    protected BaseLoadBuilder self() {
        return this;
    }
}

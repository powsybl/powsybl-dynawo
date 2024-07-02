/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.Network;

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

    public static WeccBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static WeccBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, WeccBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new WeccBuilder(network, modelConfig, reportNode);
    }

    public static ModelConfigsLibsInfo getSupportedLibs() {
        return MODEL_CONFIGS;
    }

    protected WeccBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
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

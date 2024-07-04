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
public class GridFormingConverterBuilder extends AbstractGeneratorBuilder<GridFormingConverterBuilder> {

    public static final String CATEGORY = "GRID_FORMING_CONVERTER";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static GridFormingConverterBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static GridFormingConverterBuilder of(Network network, ReportNode reportNode) {
        return new GridFormingConverterBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static GridFormingConverterBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static GridFormingConverterBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, GridFormingConverterBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new GridFormingConverterBuilder(network, modelConfig, reportNode);
    }

    public static ModelInfos getSupportedModelInfos() {
        return MODEL_CONFIGS;
    }

    protected GridFormingConverterBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
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

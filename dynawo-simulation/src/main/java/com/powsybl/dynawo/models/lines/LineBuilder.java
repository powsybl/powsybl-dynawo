/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.lines;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LineBuilder extends AbstractEquipmentModelBuilder<Line, LineBuilder> {

    public static final String CATEGORY = "BASE_LINE";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static LineBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static LineBuilder of(Network network, ReportNode reportNode) {
        return new LineBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static LineBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static LineBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, LineBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new LineBuilder(network, modelConfig, reportNode);
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

    protected LineBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.LINE, reportNode);
    }

    @Override
    protected Line findEquipment(String staticId) {
        return network.getLine(staticId);
    }

    @Override
    public StandardLine build() {
        return isInstantiable() ? new StandardLine(getEquipment(), parameterSetId, modelConfig) : null;
    }

    @Override
    protected LineBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.shunts;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.ShuntCompensator;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseShuntBuilder extends AbstractEquipmentModelBuilder<ShuntCompensator, BaseShuntBuilder> {

    public static final String CATEGORY = "BASE_SHUNT";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static BaseShuntBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static BaseShuntBuilder of(Network network, ReportNode reportNode) {
        return new BaseShuntBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static BaseShuntBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static BaseShuntBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, BaseShuntBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new BaseShuntBuilder(network, modelConfig, reportNode);
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

    protected BaseShuntBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.SHUNT_COMPENSATOR, reportNode);
    }

    @Override
    protected ShuntCompensator findEquipment(String staticId) {
        return network.getShuntCompensator(staticId);
    }

    @Override
    public BaseShunt build() {
        return isInstantiable() ? new BaseShunt(dynamicModelId, getEquipment(), parameterSetId, modelConfig) : null;
    }

    @Override
    protected BaseShuntBuilder self() {
        return this;
    }
}

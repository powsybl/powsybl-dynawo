/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.shunts;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.ShuntCompensator;

import java.util.Set;

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

    public static BaseShuntBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static BaseShuntBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, BaseShuntBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new BaseShuntBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
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
        return isInstantiable() ? new BaseShunt(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected BaseShuntBuilder self() {
        return this;
    }
}

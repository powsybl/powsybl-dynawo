/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseStaticVarCompensatorBuilder extends AbstractEquipmentModelBuilder<StaticVarCompensator, BaseStaticVarCompensatorBuilder> {

    public static final String CATEGORY = "BASE_STATIC_VAR_COMPENSATOR";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static BaseStaticVarCompensatorBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, ReportNode reportNode) {
        return new BaseStaticVarCompensatorBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, BaseStaticVarCompensatorBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new BaseStaticVarCompensatorBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected BaseStaticVarCompensatorBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.STATIC_VAR_COMPENSATOR, reportNode);
    }

    @Override
    protected StaticVarCompensator findEquipment(String staticId) {
        return network.getStaticVarCompensator(staticId);
    }

    @Override
    public BaseStaticVarCompensator build() {
        return isInstantiable() ? new BaseStaticVarCompensator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected BaseStaticVarCompensatorBuilder self() {
        return this;
    }
}

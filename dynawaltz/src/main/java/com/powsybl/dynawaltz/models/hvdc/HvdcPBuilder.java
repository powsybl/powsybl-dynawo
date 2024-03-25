/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    private static final String CATEGORY = "hvdcP";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static HvdcPBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static HvdcPBuilder of(Network network, ReportNode reportNode) {
        return new HvdcPBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static HvdcPBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static HvdcPBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, HvdcPBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new HvdcPBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected HvdcPBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.HVDC_LINE, reportNode);
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
    protected HvdcLine findEquipment(String staticId) {
        return network.getHvdcLine(staticId);
    }

    @Override
    protected HvdcPBuilder self() {
        return this;
    }
}

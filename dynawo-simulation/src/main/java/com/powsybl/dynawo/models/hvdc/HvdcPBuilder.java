/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    public static final String CATEGORY = "HVDC_P";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);
    private static final Function<TwoSides, String> EVENT_VAR_NAME_SUPPLIER = ts -> String.format("hvdc_switchOffSignal2%s", SideUtils.getSideSuffix(ts));

    public static HvdcPBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static HvdcPBuilder of(Network network, ReportNode reportNode) {
        return new HvdcPBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static HvdcPBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static HvdcPBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, HvdcPBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new HvdcPBuilder(network, modelConfig, reportNode);
    }

    public static Collection<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    protected HvdcPBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, IdentifiableType.HVDC_LINE, reportNode, EVENT_VAR_NAME_SUPPLIER);
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

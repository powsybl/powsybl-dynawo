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
import com.powsybl.iidm.network.*;

import java.util.Collection;
import java.util.function.Function;

import static com.powsybl.iidm.network.IdentifiableType.HVDC_LINE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVscBuilder extends AbstractHvdcBuilder<HvdcVscBuilder> {

    public static final String CATEGORY = "HVDC_VSC";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);
    private static final BuilderEquipment.EquipmentPredicate<HvdcLine> IS_VSC = (eq, f, r) -> {
        if (HvdcConverterStation.HvdcType.VSC != eq.getConverterStation1().getHvdcType()) {
            BuilderReports.reportWrongHvdcType(r, f, eq.getId(), HvdcConverterStation.HvdcType.VSC);
            return false;
        }
        return true;
    };
    private static final Function<TwoSides, String> EVENT_VAR_NAME_SUPPLIER = ts -> String.format("hvdc_Conv%s_switchOffSignal2", ts.getNum());

    public static HvdcVscBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static HvdcVscBuilder of(Network network, ReportNode reportNode) {
        return new HvdcVscBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static HvdcVscBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static HvdcVscBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, HvdcVscBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new HvdcVscBuilder(network, modelConfig, reportNode);
    }

    public static Collection<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    protected HvdcVscBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, HVDC_LINE + " VSC", IS_VSC, reportNode, EVENT_VAR_NAME_SUPPLIER);
    }

    @Override
    protected HvdcVscBuilder self() {
        return this;
    }
}

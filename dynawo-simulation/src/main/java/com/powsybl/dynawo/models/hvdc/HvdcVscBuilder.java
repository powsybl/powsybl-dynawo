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
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.*;

import java.util.Collection;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVscBuilder extends AbstractHvdcBuilder<HvdcVscBuilder> {

    public static final String CATEGORY = "HVDC_VSC";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);
    private static final HvdcVarNameHandler VSC_NAME_HANDLER = new VscVarNameHandler();
    private static final EquipmentChecker<HvdcLine> IS_VSC = (eq, f, r) -> {
        if (HvdcConverterStation.HvdcType.VSC != eq.getConverterStation1().getHvdcType()) {
            BuilderReports.reportWrongHvdcType(r, f, eq.getId(), HvdcConverterStation.HvdcType.VSC);
            return false;
        }
        return true;
    };

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

    protected HvdcVscBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, "VSC " + IdentifiableType.HVDC_LINE, parentReportNode, VSC_NAME_HANDLER);
    }

    @Override
    public HvdcVscBuilder staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment, IS_VSC);
        return self();
    }

    @Override
    public HvdcVscBuilder equipment(HvdcLine equipment) {
        builderEquipment.addEquipment(equipment, network, IS_VSC);
        return self();
    }

    @Override
    protected HvdcVscBuilder self() {
        return this;
    }
}

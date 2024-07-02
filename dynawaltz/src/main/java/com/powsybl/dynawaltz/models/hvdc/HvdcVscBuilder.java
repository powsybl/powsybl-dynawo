/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.*;

import java.util.function.Predicate;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVscBuilder extends AbstractHvdcBuilder<HvdcVscBuilder> {

    public static final String CATEGORY = "HVDC_VSC";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);
    private static final Predicate<HvdcLine> IS_VSC = eq -> HvdcConverterStation.HvdcType.VSC == eq.getConverterStation1().getHvdcType();

    public static HvdcVscBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static HvdcVscBuilder of(Network network, ReportNode reportNode) {
        return new HvdcVscBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static HvdcVscBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static HvdcVscBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, HvdcVscBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new HvdcVscBuilder(network, modelConfig, reportNode);
    }

    public static ModelConfigsLibsInfo getSupportedLibs() {
        return MODEL_CONFIGS;
    }

    protected HvdcVscBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, "VSC " + IdentifiableType.HVDC_LINE, reportNode);
        addEquipmentPredicate(IS_VSC);
    }

    @Override
    public HvdcVsc build() {
        if (isInstantiable()) {
            if (modelConfig.isDangling()) {
                return new HvdcVscDangling(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), danglingSide);
            } else {
                return new HvdcVsc(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            }
        }
        return null;
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        HvdcLine line = network.getHvdcLine(staticId);
        return line != null && IS_VSC.test(line) ? line : null;
    }

    @Override
    protected HvdcVscBuilder self() {
        return this;
    }
}

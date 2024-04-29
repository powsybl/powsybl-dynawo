/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.BuilderReports;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LineBuilder extends AbstractEquipmentModelBuilder<Line, LineBuilder> {

    private static final String CATEGORY = "baseLines";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static LineBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static LineBuilder of(Network network, ReportNode reportNode) {
        return new LineBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static LineBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static LineBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, LineBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new LineBuilder(network, modelConfig, reportNode);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
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
        return isInstantiable() ? new StandardLine(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected LineBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardBusBuilder extends AbstractBusBuilder<StandardBusBuilder> {

    public static final String CATEGORY = "BASE_BUS";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static StandardBusBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static StandardBusBuilder of(Network network, ReportNode reportNode) {
        return new StandardBusBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static StandardBusBuilder of(Network network, String lib) {
        return of(network, lib, ReportNode.NO_OP);
    }

    public static StandardBusBuilder of(Network network, String lib, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, StandardBusBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new StandardBusBuilder(network, modelConfig, reportNode);
    }

    public static ModelConfigsLibsInfo getSupportedLibs() {
        return MODEL_CONFIGS;
    }

    protected StandardBusBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
    }

    @Override
    public StandardBus build() {
        return isInstantiable() ? new StandardBus(dynamicModelId, getEquipment(), parameterSetId, "Bus") : null;
    }

    @Override
    protected StandardBusBuilder self() {
        return this;
    }
}

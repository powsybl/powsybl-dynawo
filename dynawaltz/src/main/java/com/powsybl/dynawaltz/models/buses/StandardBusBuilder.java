/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardBusBuilder extends AbstractBusBuilder<StandardBusBuilder> {

    private static final String CATEGORY = "baseBuses";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static StandardBusBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static StandardBusBuilder of(Network network, Reporter reporter) {
        return new StandardBusBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static StandardBusBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static StandardBusBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, StandardBusBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new StandardBusBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected StandardBusBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
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
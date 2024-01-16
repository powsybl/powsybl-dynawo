/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformerBuilder extends AbstractLoadModelBuilder<LoadOneTransformerBuilder> {

    private static final String CATEGORY = "loadsOneTransformer";
    private static final Map<String, ModelConfig> LIBS = ModelConfigsSingleton.getInstance().getModelConfigs(CATEGORY);

    public static LoadOneTransformerBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static LoadOneTransformerBuilder of(Network network, Reporter reporter) {
        return new LoadOneTransformerBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static LoadOneTransformerBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static LoadOneTransformerBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, LoadOneTransformerBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new LoadOneTransformerBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected LoadOneTransformerBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public LoadOneTransformer build() {
        return isInstantiable() ? new LoadOneTransformer(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected LoadOneTransformerBuilder self() {
        return this;
    }
}

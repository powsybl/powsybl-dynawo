/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformers;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersBuilder> {

    private static final String CATEGORY = "loadsTwoTransformers";
    private static final Map<String, ModelConfig> LIBS = ModelConfigsSingleton.getInstance().getModelConfigs(CATEGORY);

    public static LoadTwoTransformersBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static LoadTwoTransformersBuilder of(Network network, Reporter reporter) {
        return new LoadTwoTransformersBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static LoadTwoTransformersBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static LoadTwoTransformersBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, LoadTwoTransformersBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new LoadTwoTransformersBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected LoadTwoTransformersBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public LoadTwoTransformers build() {
        return isInstantiable() ? new LoadTwoTransformers(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib()) : null;
    }

    @Override
    protected LoadTwoTransformersBuilder self() {
        return this;
    }
}
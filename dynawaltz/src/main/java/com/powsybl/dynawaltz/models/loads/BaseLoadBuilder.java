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
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseLoadBuilder extends AbstractLoadModelBuilder<BaseLoadBuilder> {

    private static final String CATEGORY = "baseLoads";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static BaseLoadBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static BaseLoadBuilder of(Network network, Reporter reporter) {
        return new BaseLoadBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static BaseLoadBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static BaseLoadBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, BaseLoadBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new BaseLoadBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected BaseLoadBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public BaseLoad build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new BaseLoadControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            } else {
                return new BaseLoad(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            }
        } else {
            return null;
        }
    }

    @Override
    protected BaseLoadBuilder self() {
        return this;
    }
}
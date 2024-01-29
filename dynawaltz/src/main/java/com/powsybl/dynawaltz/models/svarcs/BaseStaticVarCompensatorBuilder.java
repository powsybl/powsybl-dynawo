/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseStaticVarCompensatorBuilder extends AbstractEquipmentModelBuilder<StaticVarCompensator, BaseStaticVarCompensatorBuilder> {

    private static final String CATEGORY = "staticVarCompensators";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static BaseStaticVarCompensatorBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, Reporter reporter) {
        return new BaseStaticVarCompensatorBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static BaseStaticVarCompensatorBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, BaseStaticVarCompensatorBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new BaseStaticVarCompensatorBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected BaseStaticVarCompensatorBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, IdentifiableType.STATIC_VAR_COMPENSATOR, reporter);
    }

    @Override
    protected StaticVarCompensator findEquipment(String staticId) {
        return network.getStaticVarCompensator(staticId);
    }

    @Override
    public BaseStaticVarCompensator build() {
        return isInstantiable() ? new BaseStaticVarCompensator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected BaseStaticVarCompensatorBuilder self() {
        return this;
    }
}

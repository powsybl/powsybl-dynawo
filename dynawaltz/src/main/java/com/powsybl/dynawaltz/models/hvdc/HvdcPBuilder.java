/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    private static final String CATEGORY = "hvdcP";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static HvdcPBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static HvdcPBuilder of(Network network, Reporter reporter) {
        return new HvdcPBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static HvdcPBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static HvdcPBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, HvdcPBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new HvdcPBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected HvdcPBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, IdentifiableType.HVDC_LINE, reporter);
    }

    @Override
    public HvdcP build() {
        if (isInstantiable()) {
            if (modelConfig.isDangling()) {
                return new HvdcPDangling(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib(), danglingSide);
            } else {
                return new HvdcP(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib());
            }
        }
        return null;
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        return network.getHvdcLine(staticId);
    }

    @Override
    protected HvdcPBuilder self() {
        return this;
    }
}

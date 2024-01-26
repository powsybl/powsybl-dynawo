/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LineBuilder extends AbstractEquipmentModelBuilder<Line, LineBuilder> {

    private static final String CATEGORY = "baseLines";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static LineBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static LineBuilder of(Network network, Reporter reporter) {
        return new LineBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static LineBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static LineBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, LineBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new LineBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected LineBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, IdentifiableType.LINE, reporter);
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

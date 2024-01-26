/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatioBuilder extends AbstractEquipmentModelBuilder<TwoWindingsTransformer, TransformerFixedRatioBuilder> {

    private static final String CATEGORY = "transformers";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static TransformerFixedRatioBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, Reporter reporter) {
        return new TransformerFixedRatioBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static TransformerFixedRatioBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static TransformerFixedRatioBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, TransformerFixedRatioBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new TransformerFixedRatioBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected TransformerFixedRatioBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, IdentifiableType.TWO_WINDINGS_TRANSFORMER, reporter);
    }

    @Override
    protected TwoWindingsTransformer findEquipment(String staticId) {
        return network.getTwoWindingsTransformer(staticId);
    }

    @Override
    public TransformerFixedRatio build() {
        return isInstantiable() ? new TransformerFixedRatio(dynamicModelId, getEquipment(), parameterSetId, modelConfig.lib()) : null;
    }

    @Override
    protected TransformerFixedRatioBuilder self() {
        return this;
    }
}

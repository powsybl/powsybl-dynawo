/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsHandler;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class UnderVoltageAutomatonBuilder extends AbstractAutomatonModelBuilder<UnderVoltageAutomatonBuilder> {

    private static final String CATEGORY = "underVoltages";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigsNew(CATEGORY);

    protected final BuilderEquipment<Generator> generator;

    public static UnderVoltageAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static UnderVoltageAutomatonBuilder of(Network network, Reporter reporter) {
        return new UnderVoltageAutomatonBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reporter);
    }

    public static UnderVoltageAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static UnderVoltageAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, UnderVoltageAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new UnderVoltageAutomatonBuilder(network, MODEL_CONFIGS.getModelConfig(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return MODEL_CONFIGS.getSupportedLibs();
    }

    protected UnderVoltageAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        generator = new BuilderEquipment<>(IdentifiableType.GENERATOR, "generator");
    }

    public UnderVoltageAutomatonBuilder generator(String staticId) {
        generator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= generator.checkEquipmentData(reporter);
    }

    @Override
    public UnderVoltageAutomaton build() {
        return isInstantiable() ? new UnderVoltageAutomaton(dynamicModelId, parameterSetId, generator.getEquipment(), getLib()) : null;
    }

    @Override
    protected UnderVoltageAutomatonBuilder self() {
        return this;
    }
}

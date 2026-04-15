/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.List;

import static com.powsybl.dynawo.models.generators.GeneratorProperties.DEFAULT_OMEGA_REF_PU;
import static com.powsybl.dynawo.models.generators.GeneratorProperties.DEFAULT_RUNNING;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Olivier Perrin {@literal <olivier.perrin at rte-france.com>}
 */
public class SynchronizedGenerator extends BaseGenerator implements FrequencySynchronizedModel {
    private static final ComponentDescription DEFAULT_COMPONENT_DESCRIPTION = new Description();

    protected SynchronizedGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig,
                isGeneratorCustom(modelConfig) ? CustomGeneratorComponent.fromModelConfig(modelConfig) : DEFAULT_COMPONENT_DESCRIPTION);
    }

    @Override
    public String getOmegaRefPuVarName() {
        return getComponentDescription().omegaRefPu();
    }

    @Override
    public String getRunningVarName() {
        return getComponentDescription().running();
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }

    static class Description extends BaseGenerator.Description implements ComponentDescription {
        @Override
        public String omegaRefPu() {
            return DEFAULT_OMEGA_REF_PU;
        }

        @Override
        public String running() {
            return DEFAULT_RUNNING;
        }
    }
}

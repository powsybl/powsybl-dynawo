/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class UnderVoltageTest {

    @Test
    void connectionToDefaultGeneratorException() {
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());
        List<BlackBoxModel> dynamicModels = new ArrayList<>();
        dynamicModels.add(UnderVoltageAutomationSystemBuilder.of(network)
                .dynamicModelId("Under_voltage")
                .parameterSetId("UV")
                .generator("GEN")
                .build());
        DynawoSimulationContext.Builder contextBuilder = new DynawoSimulationContext.Builder(network, dynamicModels);
        assertThatThrownBy(contextBuilder::build)
                .isInstanceOf(PowsyblException.class)
                .hasMessage("Default model DefaultGenerator for GEN does not implement SpecifiedGeneratorModel interface");
    }
}

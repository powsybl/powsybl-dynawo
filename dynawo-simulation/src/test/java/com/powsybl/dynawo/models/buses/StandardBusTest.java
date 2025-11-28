/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class StandardBusTest {

    @Test
    void connectionToModelWithoutDynamicModelException() {
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());
        List<BlackBoxModel> dynamicModels = new ArrayList<>();
        dynamicModels.add(StandardBusBuilder.of(network)
                .staticId("NHV1")
                .parameterSetId("SB")
                .build());
        DynawoSimulationContext.Builder contextBuilder = new DynawoSimulationContext.Builder(network, dynamicModels);
        assertThatThrownBy(contextBuilder::build)
                .isInstanceOf(PowsyblException.class)
                .hasMessage("At least one dynamic model forbid default models and the equipment NHV1_NHV2_1 does not possess a dynamic model");
    }
}

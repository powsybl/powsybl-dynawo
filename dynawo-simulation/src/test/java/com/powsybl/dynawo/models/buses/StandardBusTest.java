/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                .dynamicModelId("BBM_NHV1")
                .staticId("NHV1")
                .parameterSetId("SB")
                .build());
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        List<BlackBoxModel> events = Collections.emptyList();
        List<OutputVariable> curves = Collections.emptyList();
        PowsyblException e = assertThrows(PowsyblException.class, () -> new DynawoSimulationContext(network, workingVariantId, dynamicModels, events, curves, parameters, dynawoParameters));
        assertEquals("The equipment NHV1_NHV2_1 linked to the StandardBus NHV1 does not possess a dynamic model", e.getMessage());
    }
}

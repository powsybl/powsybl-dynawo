/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
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
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class StandardBusTest {

    @Test
    void connectionToModelWithoutDynamicModelException() {
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());
        List<BlackBoxModel> dynamicModels = new ArrayList<>();
        network.getBusBreakerView().getBuses().forEach(b -> {
            if (b.getId().equals("NHV1")) {
                dynamicModels.add(new StandardBus("BBM_" + b.getId(), b, "SB"));
            }
        });
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        List<BlackBoxModel> events = Collections.emptyList();
        List<Curve> curves = Collections.emptyList();
        PowsyblException e = assertThrows(PowsyblException.class, () -> new DynaWaltzContext(network, workingVariantId, dynamicModels, events, curves, parameters, dynawoParameters));
        assertEquals("The equipment NHV1_NHV2_1 linked to the standard bus NHV1 does not possess a dynamic model", e.getMessage());
    }
}

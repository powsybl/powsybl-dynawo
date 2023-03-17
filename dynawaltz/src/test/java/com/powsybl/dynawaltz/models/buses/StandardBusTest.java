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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class StandardBusTest {

    private StandardBus standardBus;

    @BeforeEach
    void setUp() {
        standardBus = new StandardBus("dynamicModelId", "staticId", "parameterSetId");
    }

    @Test
    void getModelsConnectedToException() {
        DynaWaltzContext dynaWaltzContext = mock(DynaWaltzContext.class);
        Network network = mock(Network.class);
        Network.BusBreakerView busBreakerView = mock(Network.BusBreakerView.class);
        when(dynaWaltzContext.getNetwork()).thenReturn(network);
        when(network.getBusBreakerView()).thenReturn(busBreakerView);
        when(busBreakerView.getBus(anyString())).thenReturn(null);

        PowsyblException e = assertThrows(PowsyblException.class, () -> standardBus.createMacroConnections(dynaWaltzContext));
        assertEquals("Bus static id unknown: staticId", e.getMessage());
    }
}

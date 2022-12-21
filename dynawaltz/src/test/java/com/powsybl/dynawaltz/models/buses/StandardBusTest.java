/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.buses;

import com.google.errorprone.annotations.Var;
import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.NetworkModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.iidm.network.Network;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class StandardBusTest {

    private StandardBus standardBus;

    @Before
    public void setUp() {
        standardBus = new StandardBus("dynamicModelId","staticId", "parameterSetId");
    }

    @Test(expected = PowsyblException.class)
    public void getVarConnectionsWithException() {
        Model model = mock(Model.class);
        standardBus.getVarConnectionsWith(model);
    }

    @Test
    public void getVarConnectionsWithGetValues() {
        GeneratorModel generatorModel = mock(GeneratorModel.class);
        List<VarConnection> varConnectionList = standardBus.getVarConnectionsWith(generatorModel);
        assertNotNull(varConnectionList);
        assertEquals(2, varConnectionList.size());

        VarConnection firstVarConnection = varConnectionList.get(0);
        assertEquals(firstVarConnection.getVar1(), standardBus.getTerminalVarName());
        assertEquals(firstVarConnection.getVar2(), generatorModel.getTerminalVarName());

        VarConnection secondVarConnection = varConnectionList.get(1);
        assertEquals(secondVarConnection.getVar1(), standardBus.getSwitchOffSignalVarName());
        assertEquals(secondVarConnection.getVar2(), generatorModel.getSwitchOffSignalNodeVarName());
    }

    @Test(expected = PowsyblException.class)
    public void getModelsConnectedToException() {
        DynaWaltzContext dynaWaltzContext = mock(DynaWaltzContext.class);
        Network network = mock(Network.class);
        Network.BusBreakerView busBreakerView = mock(Network.BusBreakerView.class);
        when(dynaWaltzContext.getNetwork()).thenReturn(network);
        when(network.getBusBreakerView()).thenReturn(busBreakerView);
        when(busBreakerView.getBus(anyString())).thenReturn(null);

        standardBus.getModelsConnectedTo(dynaWaltzContext);
    }
}

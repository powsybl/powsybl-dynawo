/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.iidm.network.Network;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class OmegaRefTest {

    private OmegaRef omegaRef;

    @Before
    public void setUp() {
        omegaRef = new OmegaRef(Collections.emptyList());
    }

    @Test(expected = PowsyblException.class)
    public void getVarConnectionsWithException() {
        Model model = mock(Model.class);
        omegaRef.getVarConnectionsWith(model);
    }

    @Test
    public void getVarConnectionsWithVarConnectionBus() {
        BusModel busModel = mock(BusModel.class);
        List<VarConnection> varConnectionList = omegaRef.getVarConnectionsWith(busModel);
        assertNotNull(varConnectionList);
        assertEquals(1, varConnectionList.size());
        VarConnection varConnection = varConnectionList.get(0);
        assertEquals("numcc_node_@INDEX@", varConnection.getVar1());
        assertEquals(busModel.getNumCCVarName(), varConnection.getVar2());
    }

    @Test(expected = PowsyblException.class)
    public void writeMacroConnectException() throws XMLStreamException {
        Model model = mock(Model.class);
        omegaRef.writeMacroConnect(null, null, null, model);
    }
}

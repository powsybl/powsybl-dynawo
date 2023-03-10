/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.buses.BusModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
class OmegaRefTest {

    private OmegaRef omegaRef;

    @BeforeEach
    void setUp() {
        omegaRef = new OmegaRef(Collections.emptyList());
    }

    @Test
    void getVarConnectionsWithException() {
        Model model = mock(Model.class);
        PowsyblException e = assertThrows(PowsyblException.class, () -> omegaRef.getVarConnectionsWith(model));
        assertEquals("OmegaRef can only connect to GeneratorModel and BusModel", e.getMessage());
    }

    @Test
    void getVarConnectionsWithVarConnectionBus() {
        BusModel busModel = mock(BusModel.class);
        List<VarConnection> varConnectionList = omegaRef.getVarConnectionsWith(busModel);
        assertNotNull(varConnectionList);
        assertEquals(1, varConnectionList.size());
        VarConnection varConnection = varConnectionList.get(0);
        assertEquals("numcc_node_@INDEX@", varConnection.getVar1());
        assertEquals(busModel.getNumCCVarName(), varConnection.getVar2());
    }

    @Test
    void writeMacroConnectException() {
        Model model = mock(Model.class);
        PowsyblException e = assertThrows(PowsyblException.class, () -> omegaRef.writeMacroConnect(null, null, null, model));
        assertEquals("OmegaRef can only connect to OmegaRefGeneratorModel and BusModel", e.getMessage());
    }
}

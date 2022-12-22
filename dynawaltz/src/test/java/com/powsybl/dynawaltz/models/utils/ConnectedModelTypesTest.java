/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class ConnectedModelTypesTest {

    @Test
    public void twoConnectedModelTypesEqual() {
        ConnectedModelTypes connectedModelTypes1 = ConnectedModelTypes.of("a", "b");
        ConnectedModelTypes connectedModelTypes2 = ConnectedModelTypes.of("b", "a");

        assertEquals(connectedModelTypes1, connectedModelTypes2);
    }

    @Test
    public void twoConnectedModelTypesNotEqual() {
        ConnectedModelTypes connectedModelTypes1 = ConnectedModelTypes.of("a", "b");
        ConnectedModelTypes connectedModelTypes2 = ConnectedModelTypes.of("a", "c");

        assertNotEquals(connectedModelTypes1, connectedModelTypes2);
    }
}

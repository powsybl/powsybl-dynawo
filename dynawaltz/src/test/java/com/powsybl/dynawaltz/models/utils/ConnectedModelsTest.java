/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.utils;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
class ConnectedModelsTest {

    @Test
    void twoConnectedModelsEqual() {
        BlackBoxModel bbm1 = mock(BlackBoxModel.class);
        BlackBoxModel bbm2 = mock(BlackBoxModel.class);

        ConnectedModels connectedModels1 = ConnectedModels.of(bbm1, bbm2);
        ConnectedModels connectedModels2 = ConnectedModels.of(bbm2, bbm1);

        assertEquals(connectedModels1, connectedModels2);
    }

    @Test
    void twoConnectedModelsNotEqual() {
        ConnectedModels connectedModels1 = ConnectedModels.of(mock(BlackBoxModel.class), mock(BlackBoxModel.class));
        ConnectedModels connectedModels2 = ConnectedModels.of(mock(BlackBoxModel.class), mock(BlackBoxModel.class));

        assertNotEquals(connectedModels1, connectedModels2);
    }
}

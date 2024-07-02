/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems;

import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;

/**
 * Indicates the connection state of a dynamic model
 * Used when dynamic model try to connect to a pure dynamic model
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ConnectionStatefulModel {

    /**
     * Dynamic model connection state
     */
    enum ConnectionState {
        /**
         * Connected to specified equipments
         */
        CONNECTED,
        /**
         * Can not be connected
         */
        NOT_CONNECTED,
        /**
         * Not connected yet
         */
        NOT_SET
    }

    /**
     * Verifies if the model is connected, if NOT_SET try to createMacroConnections
     */
    boolean isConnectedOrConnect(MacroConnectionsAdder adder);
}

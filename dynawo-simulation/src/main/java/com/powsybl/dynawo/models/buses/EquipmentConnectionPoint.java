/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.models.Model;
import com.powsybl.iidm.network.TwoSides;

import java.util.Optional;

/**
 * Interface use for buses by equipments for connection with the network
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface EquipmentConnectionPoint extends Model {

    String getTerminalVarName();

    default String getTerminalVarName(TwoSides side) {
        return getTerminalVarName();
    }

    Optional<String> getSwitchOffSignalVarName();

    default Optional<String> getSwitchOffSignalVarName(TwoSides side) {
        return getSwitchOffSignalVarName();
    }
}

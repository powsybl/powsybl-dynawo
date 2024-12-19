/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.models.Model;

/**
 * View of signal N equipment from the bus to which it is connected
 * Used by Signal N in order to get phi var name
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface BusOfSignalNModel extends Model {

    String getPhiVarName();
}

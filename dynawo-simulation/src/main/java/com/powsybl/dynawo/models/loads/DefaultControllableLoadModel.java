/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.models.events.PControllableEquipmentModel;
import com.powsybl.dynawo.models.events.QControllableEquipmentModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DefaultControllableLoadModel extends PControllableEquipmentModel, QControllableEquipmentModel {

    String getDeltaPVarName();

    String getDeltaQVarName();
}

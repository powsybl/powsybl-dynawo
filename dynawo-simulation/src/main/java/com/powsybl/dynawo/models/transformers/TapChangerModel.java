/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.transformers;

import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.VarConnection;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.HIGH_VOLTAGE;
import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface TapChangerModel extends Model {

    List<VarConnection> getTapChangerBlockerVarConnections();

    default String getTapChangerBlockingVarName(TransformerSide side) {
        return "tapChangerBlocking_blocked" + (NONE == side ? HIGH_VOLTAGE.getSideSuffix() : side.getSideSuffix());
    }

}

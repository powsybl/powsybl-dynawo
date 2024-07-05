/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.iidm.network.Bus;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface FrequencySynchronizedModel extends Model {

    default List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    default List<VarConnection> getSetPointVarConnections() {
        return List.of(
                new VarConnection("setPoint_setPoint", getOmegaRefPuVarName())
        );
    }

    default double getWeightGen(DynawoSimulationParameters dynawoSimulationParameters) {
        return 0;
    }

    String getOmegaRefPuVarName();

    String getRunningVarName();

    String getStaticId();

    Bus getConnectableBus();
}

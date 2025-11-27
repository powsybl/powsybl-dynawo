/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedWeccGen extends WeccGen implements FrequencySynchronizedModel {

    protected SynchronizedWeccGen(Generator generator, String parameterSetId, ModelConfig modelConfig, String weccPrefix) {
        super(generator, parameterSetId, modelConfig, weccPrefix);
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@_value", getOmegaRefPuVarName())
                // new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public String getOmegaRefPuVarName() {
        return weccPrefix + "_omegaRefPu";
    }

    @Override
    public String getRunningVarName() {
        return weccPrefix + "_injector_running";
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}

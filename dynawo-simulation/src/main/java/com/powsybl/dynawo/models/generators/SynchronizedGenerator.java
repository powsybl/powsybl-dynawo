/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.List;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedGenerator extends AbstractGenerator implements FrequencySynchronizedModel {

    protected SynchronizedGenerator(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public List<VarConnection> getSetPointVarConnections() {
        return List.of(
                new VarConnection("setPoint_setPoint", getOmegaRefPuVarName())
        );
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "generator_omegaRefPu";
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}

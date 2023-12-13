/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedWeccGen extends WeccGen implements FrequencySynchronizedModel {

    public SynchronizedWeccGen(String dynamicModelId, Generator generator, String parameterSetId, String weccLib, String weccPrefix) {
        super(dynamicModelId, generator, parameterSetId, weccLib, weccPrefix);
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

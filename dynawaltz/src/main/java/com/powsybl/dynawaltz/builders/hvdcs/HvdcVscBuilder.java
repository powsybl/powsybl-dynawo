/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.hvdcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVscBuilder extends AbstractHvdcBuilder<HvdcVscBuilder> {

    public HvdcVscBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public HvdcVsc build() {
        if (isInstantiable()) {
            if (modelConfig.isDangling()) {
                return new HvdcVscDangling(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib(), danglingSide);
            } else {
                return new HvdcVsc(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected HvdcVscBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.hvdcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.hvdc.HvdcP;
import com.powsybl.dynawaltz.models.hvdc.HvdcPDangling;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    public HvdcPBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public HvdcPBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public HvdcP build() {
        if (isInstantiable()) {
            if (equipmentConfig.isDangling()) {
                new HvdcPDangling(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), danglingSide);
            } else {
                new HvdcP(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected HvdcPBuilder self() {
        return this;
    }
}

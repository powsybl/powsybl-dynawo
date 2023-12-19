/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardBusBuilder extends AbstractBusBuilder<StandardBusBuilder> {

    public static final String LIB = "Bus";

    public StandardBusBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public StandardBusBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new StandardBus(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected StandardBusBuilder self() {
        return this;
    }
}

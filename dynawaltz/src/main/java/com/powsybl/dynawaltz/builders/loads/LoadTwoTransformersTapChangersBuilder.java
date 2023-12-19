/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersTapChangersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersTapChangersBuilder> {

    public static final String LIB = "LoadTwoTransformersTapChangers";

    public LoadTwoTransformersTapChangersBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadTwoTransformersTapChangersBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadTwoTransformersTapChangersBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadTwoTransformersTapChangers build() {
        return isInstantiable() ? new LoadTwoTransformersTapChangers(dynamicModelId, getEquipment(), parameterSetId, LIB) : null;
    }

    @Override
    protected LoadTwoTransformersTapChangersBuilder self() {
        return this;
    }
}

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
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformers;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersBuilder> {

    public static final String LIB = "LoadTwoTransformers";

    public LoadTwoTransformersBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadTwoTransformersBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadTwoTransformersBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadTwoTransformers build() {
        return isInstantiable() ? new LoadTwoTransformers(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadTwoTransformersBuilder self() {
        return this;
    }
}

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
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformerTapChangerBuilder extends AbstractLoadModelBuilder<LoadOneTransformerTapChangerBuilder> {

    //TODO virer les lib dans les builders ?
    public static final String LIB = "LoadOneTransformerTapChanger";

    public LoadOneTransformerTapChangerBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadOneTransformerTapChangerBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadOneTransformerTapChangerBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadOneTransformerTapChanger build() {
        return isInstantiable() ? new LoadOneTransformerTapChanger(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadOneTransformerTapChangerBuilder self() {
        return this;
    }
}

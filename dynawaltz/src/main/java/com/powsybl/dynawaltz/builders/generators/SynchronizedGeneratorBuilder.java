/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorControllable;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder<SynchronizedGeneratorBuilder> {

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public SynchronizedGenerator build() {
        if (isInstantiable()) {
            if (equipmentConfig.isControllable()) {
                new SynchronizedGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            } else {
                new SynchronizedGenerator(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected SynchronizedGeneratorBuilder self() {
        return this;
    }
}

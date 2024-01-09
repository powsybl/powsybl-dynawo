/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorControllable;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder<SynchronizedGeneratorBuilder> {

    public SynchronizedGeneratorBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public SynchronizedGenerator build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new SynchronizedGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib());
            } else {
                return new SynchronizedGenerator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected SynchronizedGeneratorBuilder self() {
        return this;
    }
}

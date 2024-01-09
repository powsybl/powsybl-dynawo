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
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen;
import com.powsybl.dynawaltz.models.generators.WeccGen;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WeccBuilder extends AbstractGeneratorBuilder<WeccBuilder> {

    public WeccBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public WeccGen build() {
        if (isInstantiable()) {
            if (modelConfig.isSynchronized()) {
                return new SynchronizedWeccGen(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib(), modelConfig.getInternalModelPrefix());
            } else {
                return new WeccGen(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib(), modelConfig.getInternalModelPrefix());
            }
        }
        return null;
    }

    @Override
    protected WeccBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder<SynchronousGeneratorBuilder> {

    public SynchronousGeneratorBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    public SynchronousGeneratorBuilder(Network network, ModelConfig modelConfig) {
        super(network, modelConfig);
    }

    protected EnumGeneratorComponent getGeneratorComponent() {
        boolean aux = modelConfig.hasAuxiliary();
        boolean transformer = modelConfig.hasTransformer();
        if (aux && transformer) {
            return EnumGeneratorComponent.AUXILIARY_TRANSFORMER;
        } else if (transformer) {
            return EnumGeneratorComponent.TRANSFORMER;
        } else if (aux) {
            throw new PowsyblException("Generator component auxiliary without transformer is not supported");
        }
        return EnumGeneratorComponent.NONE;
    }

    @Override
    public SynchronousGenerator build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new SynchronousGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib(), getGeneratorComponent());
            } else {
                return new SynchronousGenerator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib(), getGeneratorComponent());
            }
        }
        return null;
    }

    @Override
    protected SynchronousGeneratorBuilder self() {
        return this;
    }
}

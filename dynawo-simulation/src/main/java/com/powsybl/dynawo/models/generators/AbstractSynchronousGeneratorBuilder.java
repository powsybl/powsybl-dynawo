/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractSynchronousGeneratorBuilder<R extends AbstractSynchronousGeneratorBuilder<R>> extends AbstractGeneratorBuilder<R> {

    protected AbstractSynchronousGeneratorBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
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
                return new SynchronousGeneratorControllable(getEquipment(), parameterSetId, modelConfig, getGeneratorComponent());
            } else {
                return new SynchronousGenerator(getEquipment(), parameterSetId, modelConfig, getGeneratorComponent());
            }
        }
        return null;
    }
}

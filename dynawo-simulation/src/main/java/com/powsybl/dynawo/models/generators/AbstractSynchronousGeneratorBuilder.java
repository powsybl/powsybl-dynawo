/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

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

    @Override
    public SynchronousGenerator build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return isGeneratorCustom() ? new CustomSynchronousGeneratorControllable(getEquipment(), parameterSetId, modelConfig)
                        : new SynchronousGeneratorControllable(getEquipment(), parameterSetId, modelConfig);
            } else {
                return isGeneratorCustom() ? new CustomSynchronousGenerator(getEquipment(), parameterSetId, modelConfig)
                        : new SynchronousGenerator(getEquipment(), parameterSetId, modelConfig);
            }
        }
        return null;
    }
}

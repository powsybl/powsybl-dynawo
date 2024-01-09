/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.EquipmentModelBuilder;
import com.powsybl.dynawaltz.models.loads.BaseLoad;
import com.powsybl.dynawaltz.models.loads.BaseLoadControllable;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseLoadBuilder extends AbstractLoadModelBuilder<BaseLoadBuilder> implements EquipmentModelBuilder<BaseLoadBuilder> {

    public BaseLoadBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public BaseLoad build() {
        if (isInstantiable()) {
            if (modelConfig.isControllable()) {
                return new BaseLoadControllable(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib());
            } else {
                return new BaseLoad(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib());
            }
        } else {
            return null;
        }
    }

    @Override
    protected BaseLoadBuilder self() {
        return this;
    }
}

/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.svarcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseStaticVarCompensatorBuilder extends AbstractEquipmentModelBuilder<StaticVarCompensator, BaseStaticVarCompensatorBuilder> {

    public BaseStaticVarCompensatorBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, IdentifiableType.STATIC_VAR_COMPENSATOR, reporter);
    }

    @Override
    protected StaticVarCompensator findEquipment(String staticId) {
        return network.getStaticVarCompensator(staticId);
    }

    @Override
    public BaseStaticVarCompensator build() {
        return isInstantiable() ? new BaseStaticVarCompensator(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib()) : null;
    }

    @Override
    protected BaseStaticVarCompensatorBuilder self() {
        return this;
    }
}

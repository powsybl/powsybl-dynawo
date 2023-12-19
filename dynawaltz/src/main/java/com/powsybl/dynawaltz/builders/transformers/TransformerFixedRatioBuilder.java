/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.transformers;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TransformerFixedRatioBuilder extends AbstractEquipmentModelBuilder<TwoWindingsTransformer, TransformerFixedRatioBuilder> {

    public static final String LIB = "transformers";

    public TransformerFixedRatioBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.TWO_WINDINGS_TRANSFORMER, reporter);
    }

    public TransformerFixedRatioBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    }

    @Override
    protected TwoWindingsTransformer findEquipment(String staticId) {
        return network.getTwoWindingsTransformer(staticId);
    }

    @Override
    public TransformerFixedRatio build() {
        return isInstantiable() ? new TransformerFixedRatio(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib()) : null;
    }

    @Override
    protected TransformerFixedRatioBuilder self() {
        return this;
    }
}

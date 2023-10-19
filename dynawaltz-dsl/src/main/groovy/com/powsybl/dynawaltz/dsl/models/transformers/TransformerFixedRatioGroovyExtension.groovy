/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.transformers

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class TransformerFixedRatioGroovyExtension extends AbstractEquipmentGroovyExtension {

    private static final String TRANSFORMERS = "transformers"

    TransformerFixedRatioGroovyExtension() {
        super(TRANSFORMERS)
    }

    @Override
    protected TransformerBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new TransformerBuilder(network, equipmentConfig, reporter)
    }

    static class TransformerBuilder extends AbstractEquipmentModelBuilder<TwoWindingsTransformer> {

        TransformerBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, IdentifiableType.TWO_WINDINGS_TRANSFORMER, reporter)
        }

        @Override
        protected TwoWindingsTransformer findEquipment(String staticId) {
            network.getTwoWindingsTransformer(staticId)
        }

        @Override
        TransformerFixedRatio build() {
            isInstantiable() ? new TransformerFixedRatio(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                    : null
        }
    }
}

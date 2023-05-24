/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.transformers

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class TransformerFixedRatioGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String TRANSFORMERS = "transformers"

    TransformerFixedRatioGroovyExtension() {
        super(TRANSFORMERS)
    }

    @Override
    protected TransformerBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new TransformerBuilder(network, equipmentConfig)
    }

    static class TransformerBuilder extends AbstractDynamicModelBuilder {

        TwoWindingsTransformer transformer
        EquipmentConfig equipmentConfig

        TransformerBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        void checkData() {
            super.checkData()
            transformer = network.getTwoWindingsTransformer(staticId)
            if (transformer == null) {
                throw new DslException("Transformer static id unknown: " + staticId)
            }
        }

        @Override
        TransformerFixedRatio build() {
            checkData()
            new TransformerFixedRatio(dynamicModelId, transformer, parameterSetId, equipmentConfig.lib)
        }
    }
}

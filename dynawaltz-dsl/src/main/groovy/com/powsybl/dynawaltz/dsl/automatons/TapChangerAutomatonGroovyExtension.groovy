/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.TransformerSide
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton
import com.powsybl.iidm.network.*

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class TapChangerAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String LIB = "TapChangerAutomaton"

    TapChangerAutomatonGroovyExtension() {
        modelTags = [LIB]
    }

    @Override
    protected TapChangerAutomatonBuilder createBuilder(Network network) {
        new TapChangerAutomatonBuilder(network, LIB)
    }

    static class TapChangerAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        protected final DslEquipment<Load> dslLoad
        protected TransformerSide side = TransformerSide.NONE

        TapChangerAutomatonBuilder(Network network, String lib) {
            super(network, lib)
            dslLoad = new DslEquipment<>(IdentifiableType.LOAD)
        }

        void staticId(String staticId) {
            dslLoad.addEquipment(staticId, network::getLoad)
        }

        void side(TransformerSide side) {
            this.side = side
        }

        @Override
        protected void checkData() {
            super.checkData()
            isInstantiable &= dslLoad.checkEquipmentData(LOGGER, getLib())
        }

        @Override
        TapChangerAutomaton build() {
            isInstantiable() ? new TapChangerAutomaton(dynamicModelId, parameterSetId, dslLoad.equipment, side) : null
        }
    }
}

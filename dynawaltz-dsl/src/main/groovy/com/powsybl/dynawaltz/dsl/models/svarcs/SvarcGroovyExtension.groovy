/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.svarcs

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.dynawaltz.models.svarcs.StaticVarCompensator as DynamicSvarc
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.StaticVarCompensator as StaticSvarc

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class SvarcGroovyExtension extends AbstractEquipmentGroovyExtension {

    protected static final String SVARC = "staticVarCompensators"

    SvarcGroovyExtension() {
        super(SVARC)
    }

    @Override
    protected SvcBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new SvcBuilder(network, equipmentConfig, reporter)
    }

    static class SvcBuilder extends AbstractEquipmentModelBuilder<StaticSvarc> {

        SvcBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, IdentifiableType.STATIC_VAR_COMPENSATOR, reporter)
        }

        @Override
        protected StaticSvarc findEquipment(String staticId) {
            network.getStaticVarCompensator(staticId)
        }

        @Override
        DynamicSvarc build() {
            isInstantiable() ? new DynamicSvarc(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                    : null
        }
    }
}

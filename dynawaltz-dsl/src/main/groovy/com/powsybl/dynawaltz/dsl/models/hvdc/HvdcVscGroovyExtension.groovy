/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class HvdcVscGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String HVDC_VSC = "hvdcVsc"

    HvdcVscGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(HVDC_VSC).keySet() as List
    }

    @Override
    protected HvdcBuilder createBuilder(Network network, String currentTag) {
        new HvdcBuilder(network, currentTag)
    }

    static class HvdcBuilder extends AbstractHvdcBuilder {

        HvdcBuilder(Network network, String tag) {
            super(network, tag)
        }

        @Override
        HvdcVsc build() {
            checkData()
            new HvdcVsc(dynamicModelId, hvdc, parameterSetId, tag)
        }
    }
}

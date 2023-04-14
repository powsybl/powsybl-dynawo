/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.svcs

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.svcs.StaticVarCompensatorModel
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.StaticVarCompensator

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SvcGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String SVC = "staticVarCompensators"

    SvcGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(SVC).keySet() as List
    }

    @Override
    protected SvcBuilder createBuilder(Network network, String currentTag) {
        new SvcBuilder(network, currentTag)
    }

    static class SvcBuilder extends AbstractDynamicModelBuilder {

        StaticVarCompensator svc
        String tag

        SvcBuilder(Network network, String tag) {
            super(network)
            this.tag = tag
        }

        void checkData() {
            super.checkData()
            svc = network.getStaticVarCompensator(staticId)
            if (svc == null) {
                throw new DslException("Static var compensator static id unknown: " + staticId)
            }
        }

        @Override
        StaticVarCompensatorModel build() {
            checkData()
            new StaticVarCompensatorModel(dynamicModelId, svc, parameterSetId, tag)
        }
    }
}

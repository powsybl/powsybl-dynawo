/*
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
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.hvdc.HvdcModel

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class HvdcGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String HVDC = "hvdc"

    HvdcGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(HVDC).keySet() as List
    }

    @Override
    protected HvdcBuilder createBuilder(String currentTag) {
        new HvdcBuilder(currentTag)
    }

    static class HvdcBuilder extends AbstractDynamicModelBuilder {

        String tag

        HvdcBuilder(String tag) {
            this.tag = tag
        }

        @Override
        HvdcModel build() {
            checkData()
            new HvdcModel(dynamicModelId, staticId, parameterSetId, tag)
        }
    }
}

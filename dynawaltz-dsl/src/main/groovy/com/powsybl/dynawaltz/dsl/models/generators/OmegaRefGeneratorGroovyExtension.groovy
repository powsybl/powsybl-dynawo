/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.generators.OmegaRefGenerator

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class OmegaRefGeneratorGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String CONNECTED_TO_OMEGA_REF_GENERATORS = "connectedToOmegaRefGenerators"

    OmegaRefGeneratorGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(CONNECTED_TO_OMEGA_REF_GENERATORS).keySet() as List
    }

    @Override
    protected GeneratorConnectedToOmegaRefBuilder createBuilder(String currentTag) {
        new GeneratorConnectedToOmegaRefBuilder(currentTag)
    }

    static class GeneratorConnectedToOmegaRefBuilder extends AbstractDynamicModelBuilder {

        String tag

        GeneratorConnectedToOmegaRefBuilder(String tag) {
            this.tag = tag
        }

        @Override
        OmegaRefGenerator build() {
            checkData()
            new OmegaRefGenerator(dynamicModelId, staticId, parameterSetId, tag)
        }
    }
}

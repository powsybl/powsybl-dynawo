/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String SYNCHRONOUS_GENERATORS = "synchronousGenerators"

    GeneratorSynchronousGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(SYNCHRONOUS_GENERATORS).keySet() as List
    }

    @Override
    protected GeneratorSynchronousBuilder createBuilder(String currentTag) {
        new GeneratorSynchronousBuilder(currentTag)
    }

    static class GeneratorSynchronousBuilder extends AbstractDynamicModelBuilder {

        String tag

        GeneratorSynchronousBuilder(String tag) {
            this.tag = tag
        }

        @Override
        GeneratorSynchronous build() {
            checkData()
            new GeneratorSynchronous(dynamicModelId, staticId, parameterSetId, tag)
        }
    }
}

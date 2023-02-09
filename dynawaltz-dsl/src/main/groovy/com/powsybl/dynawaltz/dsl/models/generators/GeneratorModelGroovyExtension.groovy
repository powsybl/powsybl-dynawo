/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.dynawaltz.dsl.PowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorModelGroovyExtension extends PowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String GENERATORS_CONFIG = "synchronous_generators.cfg"
    private static final String SYNCHRONOUS_GENERATORS_LIBS = "synchronousGeneratorsLibs"

    GeneratorModelGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        tags = config.parse(this.getClass().getClassLoader().getResource(GENERATORS_CONFIG)).get(SYNCHRONOUS_GENERATORS_LIBS).keySet() as List
    }

    @Override
    protected GeneratorBuilder createBuilder(String currentTag) {
        new GeneratorBuilder(currentTag)
    }

    static class GeneratorBuilder implements ModelBuilder<DynamicModel> {
        String dynamicModelId
        String staticId
        String parameterSetId
        String tag

        GeneratorBuilder(String tag) {
            this.tag = tag
        }

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }

        @Override
        GeneratorSynchronous build() {
            if (!staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }
            if (!dynamicModelId) {
                dynamicModelId = staticId
            }
            new GeneratorSynchronous(dynamicModelId, staticId, parameterSetId, tag)
        }
    }
}

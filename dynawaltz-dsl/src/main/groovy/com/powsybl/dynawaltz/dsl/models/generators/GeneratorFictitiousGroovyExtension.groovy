/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
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
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorFictitiousGroovyExtension extends PowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    GeneratorFictitiousGroovyExtension() {
        tags = ["GeneratorFictitious"]
    }

    @Override
    protected GeneratorFictitiousBuilder createBuilder(String currentTag) {
        new GeneratorFictitiousBuilder()
    }

    static class GeneratorFictitiousBuilder implements ModelBuilder<DynamicModel> {
        String dynamicModelId
        String staticId
        String parameterSetId

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
        GeneratorFictitious build() {
            if (!staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }
            if (!dynamicModelId) {
                dynamicModelId = staticId
            }
            new GeneratorFictitious(dynamicModelId, staticId, parameterSetId)
        }
    }
}

/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
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
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorFictitiousGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    GeneratorFictitiousGroovyExtension() {
        tags = ["GeneratorFictitious"]
    }

    @Override
    protected GeneratorFictitiousBuilder createBuilder(String currentTag) {
        new GeneratorFictitiousBuilder()
    }

    static class GeneratorFictitiousBuilder extends AbstractDynamicModelBuilder {

        @Override
        GeneratorFictitious build() {
            setupBuild()
            new GeneratorFictitious(dynamicModelId, staticId, parameterSetId)
        }
    }
}

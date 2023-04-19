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
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorFictitiousGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    GeneratorFictitiousGroovyExtension() {
        modelTags = ["GeneratorFictitious"]
    }

    @Override
    protected GeneratorFictitiousBuilder createBuilder(Network network, String currentTag) {
        new GeneratorFictitiousBuilder(network)
    }

    static class GeneratorFictitiousBuilder extends AbstractDynamicModelBuilder {

        Generator generator

        GeneratorFictitiousBuilder(Network network) {
            super(network)
        }

        void checkData() {
            super.checkData()
            generator = network.getGenerator(staticId)
            if (generator == null) {
                throw new DslException("Generator static id unknown: " + staticId)
            }
        }

        @Override
        GeneratorFictitious build() {
            checkData()
            new GeneratorFictitious(dynamicModelId, generator, parameterSetId)
        }
    }
}

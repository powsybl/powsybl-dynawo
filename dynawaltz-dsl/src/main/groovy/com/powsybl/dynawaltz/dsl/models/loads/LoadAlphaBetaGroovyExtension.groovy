/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.loads.LoadAlphaBeta
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadAlphaBeta</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadAlphaBetaGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String LOADS = "loadsAlphaBeta"

    LoadAlphaBetaGroovyExtension() {
        ConfigSlurper config = new ConfigSlurper()
        modelTags = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(LOADS).keySet() as List
    }

    @Override
    protected LoadAlphaBetaBuilder createBuilder(Network network, String currentTag) {
        new LoadAlphaBetaBuilder(network)
    }

    static class LoadAlphaBetaBuilder extends AbstractDynamicModelBuilder {

        Load load

        LoadAlphaBetaBuilder(Network network) {
            super(network)
        }

        void checkData() {
            super.checkData()
            load = network.getLoad(staticId)
            if (load == null) {
                throw new DslException("Load static id unknown: " + staticId)
            }
        }

        @Override
        LoadAlphaBeta build() {
            checkData()
            new LoadAlphaBeta(dynamicModelId, load, parameterSetId)
        }
    }
}

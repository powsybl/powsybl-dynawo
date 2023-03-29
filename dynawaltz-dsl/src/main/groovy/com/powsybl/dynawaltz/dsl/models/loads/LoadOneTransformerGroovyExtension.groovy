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
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadOneTransformer</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LoadOneTransformerGroovyExtension() {
        modelTags = ["LoadOneTransformer"]
    }

    @Override
    protected LoadOneTransformerBuilder createBuilder(Network network, String currentTag) {
        new LoadOneTransformerBuilder(network)
    }

    static class LoadOneTransformerBuilder extends AbstractDynamicModelBuilder {

        Load load

        LoadOneTransformerBuilder(Network network) {
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
        LoadOneTransformer build() {
            checkData()
            new LoadOneTransformer(dynamicModelId, load, parameterSetId)
        }
    }
}

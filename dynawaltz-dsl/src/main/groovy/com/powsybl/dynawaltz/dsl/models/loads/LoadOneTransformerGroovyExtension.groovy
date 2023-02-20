/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadOneTransformer</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LoadOneTransformerGroovyExtension() {
        modelTags = ["LoadOneTransformer"]
    }

    @Override
    protected LoadOneTransformerBuilder createBuilder(String currentTag) {
        new LoadOneTransformerBuilder()
    }

    static class LoadOneTransformerBuilder extends AbstractDynamicModelBuilder {
        @Override
        LoadOneTransformer build() {
            checkData()
            new LoadOneTransformer(dynamicModelId, staticId, parameterSetId)
        }
    }
}

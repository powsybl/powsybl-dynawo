/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.builders.DynamicModelBuilderUtils
import com.powsybl.dynawaltz.builders.loads.LoadOneTransformerBuilder
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.iidm.network.Network
/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadOneTransformer</pre> keyword to the DSL
 *
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerGroovyExtension extends AbstractSimpleEquipmentGroovyExtension {

    @Override
    String getLib() {
        LoadOneTransformerBuilder.LIB
    }

    @Override
    protected LoadOneTransformerBuilder createBuilder(Network network, Reporter reporter) {
        //TODO passer par utils à chaque fois ?
        DynamicModelBuilderUtils.getLoadOneTransformerBuilder(network)
    }
}

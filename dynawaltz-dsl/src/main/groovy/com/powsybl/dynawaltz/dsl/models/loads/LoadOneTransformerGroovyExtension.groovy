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
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadOneTransformer</pre> keyword to the DSL
 *
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerGroovyExtension extends AbstractSimpleEquipmentGroovyExtension {

    LoadOneTransformerGroovyExtension() {
        super("LoadOneTransformer")
    }

    @Override
    protected LoadOneTransformerBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new LoadOneTransformerBuilder(network, equipmentConfig, reporter)
    }

    static class LoadOneTransformerBuilder extends AbstractLoadModelBuilder {

        LoadOneTransformerBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        @Override
        LoadOneTransformer build() {
            isInstantiable() ? new LoadOneTransformer(dynamicModelId, equipment, parameterSetId) : null
        }
    }
}

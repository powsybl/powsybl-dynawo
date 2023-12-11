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
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.loads.BaseLoad
import com.powsybl.dynawaltz.models.loads.BaseLoadControllable
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadAlphaBeta</pre> keyword to the DSL
 *
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class BaseLoadGroovyExtension extends AbstractEquipmentGroovyExtension {

    private static final String LOADS = "baseLoads"

    BaseLoadGroovyExtension() {
        super(LOADS)
    }

    @Override
    protected LoadAlphaBetaBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new LoadAlphaBetaBuilder(network, equipmentConfig, reporter)
    }

    static class LoadAlphaBetaBuilder extends AbstractLoadModelBuilder {

        LoadAlphaBetaBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        @Override
        BaseLoad build() {
            if (isInstantiable()) {
                if (equipmentConfig.isControllable()) {
                    new BaseLoadControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                } else {
                    new BaseLoad(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                }
            } else {
                null
            }
        }
    }
}

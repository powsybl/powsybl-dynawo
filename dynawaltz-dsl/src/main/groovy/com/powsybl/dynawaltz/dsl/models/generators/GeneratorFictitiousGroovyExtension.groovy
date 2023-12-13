/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.builders.generators.GeneratorFictitiousBuilder
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.iidm.network.Network
/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorFictitiousGroovyExtension extends AbstractSimpleEquipmentGroovyExtension {

    @Override
    String getLib() {
        GeneratorFictitiousBuilder.LIB
    }

    @Override
    protected GeneratorFictitiousBuilder createBuilder(Network network, Reporter reporter) {
        new GeneratorFictitiousBuilder(network, reporter)
    }
}

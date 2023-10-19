/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.lines

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.dynawaltz.models.lines.StandardLine
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Line</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LineGroovyExtension extends AbstractSimpleEquipmentGroovyExtension {

    LineGroovyExtension() {
        super("Line")
    }

    @Override
    protected LineBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new LineBuilder(network, equipmentConfig, reporter)
    }

    static class LineBuilder extends AbstractEquipmentModelBuilder<Line> {

        LineBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, IdentifiableType.LINE, reporter)
        }

        @Override
        protected Line findEquipment(String staticId) {
            network.getLine(staticId)
        }

        @Override
        StandardLine build() {
            isInstantiable() ? new StandardLine(dynamicModelId, equipment, parameterSetId)
                    : null
        }
    }
}

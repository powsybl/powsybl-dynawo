/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.lines

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.lines.StandardLine
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Line</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LineGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LineGroovyExtension() {
        modelTags = ["Line"]
    }

    @Override
    protected LineBuilder createBuilder(Network network, String currentTag) {
        new LineBuilder(network)
    }

    static class LineBuilder extends AbstractDynamicModelBuilder {

        Line line

        LineBuilder(Network network) {
            super(network)
        }

        void checkData() {
            super.checkData()
            line = network.getLine(staticId)
            if (line == null) {
                throw new DslException("Line static id unknown: " + getStaticId())
            }
        }

        @Override
        StandardLine build() {
            checkData()
            new StandardLine(dynamicModelId, line, parameterSetId)
        }
    }
}
